package com.gamegearmatch.backend.product.dto;

import com.gamegearmatch.backend.product.domain.ConnectionType;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(

        @NotBlank(message = "상품명을 입력해 주세요.")
        @Size(max = 200)
        String name,

        @NotNull(message = "상품 종류를 선택해 주세요.")
        ProductCategory category,

        @Size(max = 100)
        String brand,

        @NotNull(message = "가격을 입력해 주세요.")
        @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @NotNull(message = "재고를 입력해 주세요.")
        @PositiveOrZero(message = "재고는 0개 이상이어야 합니다.")
        Integer stock,

        ConnectionType connectionType,

        @Size(max = 500)
        String imageUrl,

        String description,

        @Valid
        @NotNull(message = "상품 사양을 입력해 주세요.")
        ProductSpecRequest spec
) {
}