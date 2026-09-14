package com.gamegearmatch.backend.cart.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemResponse> items,
        Integer totalQuantity,
        Integer totalPrice
) {
    public static CartResponse of(Long cartId, List<CartItemResponse> items) {
        int totalQuantity = items.stream().mapToInt(CartItemResponse::quantity).sum();
        int totalPrice = items.stream().mapToInt(CartItemResponse::lineTotal).sum();
        return new CartResponse(cartId, items, totalQuantity, totalPrice);
    }
}
