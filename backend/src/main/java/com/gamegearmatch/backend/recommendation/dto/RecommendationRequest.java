package com.gamegearmatch.backend.recommendation.dto;

import com.gamegearmatch.backend.product.domain.ConnectionType;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import com.gamegearmatch.backend.recommendation.domain.GameType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecommendationRequest(
        @NotNull(message = "상품 종류를 선택해 주세요.")
        ProductCategory category,
        @NotNull(message = "게임 종류를 선택해 주세요.")
        GameType gameType,
        @Positive(message = "예산은 0원보다 커야 합니다.")
        Integer maxPrice,
        ConnectionType preferredConnection,
        boolean prioritizeLightweight
) {
}
