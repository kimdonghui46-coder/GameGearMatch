package com.gamegearmatch.backend.product.dto;

import com.gamegearmatch.backend.product.domain.ProductSpec;

public record ProductSpecResponse(
        Long id,
        Integer weight,
        Integer dpi,
        Integer pollingRate,
        Integer buttonCount,
        String switchType,
        String keyboardLayout,
        Integer noiseLevel,
        Integer responseTime,
        Integer batteryHours,
        Boolean microphone
) {

    public static ProductSpecResponse from(
            ProductSpec spec
    ) {
        return new ProductSpecResponse(
                spec.getId(),
                spec.getWeight(),
                spec.getDpi(),
                spec.getPollingRate(),
                spec.getButtonCount(),
                spec.getSwitchType(),
                spec.getKeyboardLayout(),
                spec.getNoiseLevel(),
                spec.getResponseTime(),
                spec.getBatteryHours(),
                spec.getMicrophone()
        );
    }
}