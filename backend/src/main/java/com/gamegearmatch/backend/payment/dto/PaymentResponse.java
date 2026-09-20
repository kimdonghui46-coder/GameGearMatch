package com.gamegearmatch.backend.payment.dto;

import com.gamegearmatch.backend.payment.domain.Payment;

public record PaymentResponse(Long orderId, String paymentOrderId, String status, Integer amount, String method) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getOrder().getId(), payment.getOrder().getPaymentOrderId(),
                payment.getStatus(), payment.getAmount(), payment.getMethod());
    }
}
