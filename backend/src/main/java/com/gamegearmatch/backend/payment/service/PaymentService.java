package com.gamegearmatch.backend.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamegearmatch.backend.order.domain.Order;
import com.gamegearmatch.backend.order.domain.OrderStatus;
import com.gamegearmatch.backend.order.repository.OrderRepository;
import com.gamegearmatch.backend.payment.domain.Payment;
import com.gamegearmatch.backend.payment.dto.ConfirmPaymentRequest;
import com.gamegearmatch.backend.payment.dto.PaymentResponse;
import com.gamegearmatch.backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${toss.secret-key}") private String secretKey;
    @Value("${toss.confirm-url}") private String confirmUrl;

    @Transactional
    public PaymentResponse confirm(String email, ConfirmPaymentRequest request) {
        if (secretKey == null || secretKey.isBlank()) throw new IllegalStateException("TOSS_SECRET_KEY가 설정되지 않았습니다.");
        Order order = orderRepository.findByPaymentOrderIdAndUserEmail(request.orderId(), email)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.PENDING) throw new IllegalStateException("결제 대기 상태가 아닌 주문입니다.");
        if (!order.getTotalPrice().equals(request.amount())) throw new IllegalArgumentException("결제 금액이 주문 금액과 일치하지 않습니다.");
        if (paymentRepository.existsByPaymentKey(request.paymentKey())) throw new IllegalStateException("이미 처리된 결제입니다.");

        JsonNode result = callToss(request);
        Payment payment = paymentRepository.save(Payment.builder().order(order)
                .paymentKey(request.paymentKey()).amount(request.amount())
                .method(result.path("method").asText("UNKNOWN"))
                .status(result.path("status").asText("DONE")).build());
        order.markPaid();
        return PaymentResponse.from(payment);
    }

    @Transactional
    public void refund(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new IllegalStateException("승인된 결제 정보를 찾을 수 없습니다."));
        callTossCancel(payment.getPaymentKey());
        payment.markCancelled();
    }

    private void callTossCancel(String paymentKey) {
        try {
            String body = objectMapper.writeValueAsString(Map.of("cancelReason", "고객 주문 취소"));
            String credentials = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                JsonNode error = objectMapper.readTree(response.body());
                throw new IllegalStateException(error.path("message").asText("토스 결제 취소에 실패했습니다."));
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("결제 취소 요청이 중단되었습니다.", exception);
        } catch (Exception exception) {
            if (exception instanceof IllegalStateException state) throw state;
            throw new IllegalStateException("토스 결제 취소 요청에 실패했습니다.", exception);
        }
    }

    private JsonNode callToss(ConfirmPaymentRequest request) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "paymentKey", request.paymentKey(), "orderId", request.orderId(), "amount", request.amount()));
            String credentials = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(confirmUrl))
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                JsonNode error = objectMapper.readTree(response.body());
                throw new IllegalStateException(error.path("message").asText("토스 결제 승인에 실패했습니다."));
            }
            return objectMapper.readTree(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt(); throw new IllegalStateException("결제 승인 요청이 중단되었습니다.", exception);
        } catch (Exception exception) {
            if (exception instanceof IllegalStateException state) throw state;
            throw new IllegalStateException("토스 결제 승인 요청에 실패했습니다.", exception);
        }
    }
}
