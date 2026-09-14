package com.gamegearmatch.backend.product.dto;

import com.gamegearmatch.backend.product.domain.ConnectionType;
import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import com.gamegearmatch.backend.product.domain.ProductSpec;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        ProductCategory category,
        String brand,
        Integer price,
        Integer stock,
        ConnectionType connectionType,
        String imageUrl,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProductSpecResponse spec
) {

    public static ProductResponse from(
            Product product,
            ProductSpec spec
    ) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice(),
                product.getStock(),
                product.getConnectionType(),
                product.getImageUrl(),
                product.getDescription(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                ProductSpecResponse.from(spec)
        );
    }
}