package com.gamegearmatch.backend.order.dto;

import com.gamegearmatch.backend.order.domain.Order;
import com.gamegearmatch.backend.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String paymentOrderId,
        String orderName,
        Integer totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        String recipientName,
        String recipientPhone,
        String postalCode,
        String address,
        String deliveryRequest,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(), order.getPaymentOrderId(), order.getOrderName(), order.getTotalPrice(), order.getStatus(), order.getCreatedAt(),
                order.getRecipientName(), order.getRecipientPhone(), order.getPostalCode(), order.getAddress(), order.getDeliveryRequest(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
