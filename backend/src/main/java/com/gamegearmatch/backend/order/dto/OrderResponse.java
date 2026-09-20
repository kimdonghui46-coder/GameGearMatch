package com.gamegearmatch.backend.order.dto;

import com.gamegearmatch.backend.order.domain.Order;
import com.gamegearmatch.backend.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Integer totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(), order.getTotalPrice(), order.getStatus(), order.getCreatedAt(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
