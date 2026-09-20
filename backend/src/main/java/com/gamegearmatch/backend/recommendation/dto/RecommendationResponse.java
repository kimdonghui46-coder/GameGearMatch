package com.gamegearmatch.backend.recommendation.dto;

import com.gamegearmatch.backend.product.dto.ProductResponse;

import java.util.List;

public record RecommendationResponse(
        ProductResponse product,
        double score,
        List<String> reasons
) {
}
