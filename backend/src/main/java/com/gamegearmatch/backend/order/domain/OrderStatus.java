package com.gamegearmatch.backend.order.domain;

public enum OrderStatus {
    PENDING,
    PAID,
    PREPARING,
    SHIPPING,
    DELIVERED,
    CANCELLED_REFUNDED
}
