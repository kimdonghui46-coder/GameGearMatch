package com.gamegearmatch.backend.review.dto;

import com.gamegearmatch.backend.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long productId,
        String productName,
        String userName,
        Integer rating,
        String content,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(), review.getProduct().getId(), review.getProduct().getName(), review.getUser().getName(),
                review.getRating(), review.getContent(), review.getCreatedAt()
        );
    }
}
