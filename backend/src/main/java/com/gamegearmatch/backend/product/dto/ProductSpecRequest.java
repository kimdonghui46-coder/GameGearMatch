package com.gamegearmatch.backend.product.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record ProductSpecRequest(

        @PositiveOrZero
        Integer weight,

        @PositiveOrZero
        Integer dpi,

        @PositiveOrZero
        Integer pollingRate,

        @PositiveOrZero
        Integer buttonCount,

        String switchType,

        String keyboardLayout,

        @PositiveOrZero
        Integer noiseLevel,

        @PositiveOrZero
        Integer responseTime,

        @PositiveOrZero
        Integer batteryHours,

        Boolean microphone
) {
}