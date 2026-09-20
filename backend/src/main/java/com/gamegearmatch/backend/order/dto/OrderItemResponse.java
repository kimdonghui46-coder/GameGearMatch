package com.gamegearmatch.backend.order.dto;

import com.gamegearmatch.backend.order.domain.OrderItem;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String brand,
        String imageUrl,
        Integer quantity,
        Integer orderPrice,
        Integer lineTotal
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(), item.getProduct().getId(), item.getProduct().getName(),
                item.getProduct().getBrand(), item.getProduct().getImageUrl(),
                item.getQuantity(), item.getOrderPrice(), item.getLineTotal()
        );
    }
}
