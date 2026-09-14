package com.gamegearmatch.backend.cart.dto;

import com.gamegearmatch.backend.cart.domain.CartItem;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String brand,
        Integer price,
        Integer quantity,
        Integer lineTotal,
        String imageUrl,
        Integer stock
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getBrand(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getPrice() * item.getQuantity(),
                item.getProduct().getImageUrl(),
                item.getProduct().getStock()
        );
    }
}
