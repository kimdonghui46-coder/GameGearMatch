package com.gamegearmatch.backend.order.dto;

import com.gamegearmatch.backend.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
