package com.gamegearmatch.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank @Size(max = 50) String recipientName,
        @NotBlank @Pattern(regexp = "^[0-9+\\- ]{9,20}$", message = "전화번호 형식이 올바르지 않습니다.") String recipientPhone,
        @NotBlank @Pattern(regexp = "^[0-9]{5,6}$", message = "우편번호 형식이 올바르지 않습니다.") String postalCode,
        @NotBlank @Size(max = 255) String address,
        @Size(max = 255) String deliveryRequest
) {}
