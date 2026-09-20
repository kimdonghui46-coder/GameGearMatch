package com.gamegearmatch.backend.review.controller;

import com.gamegearmatch.backend.review.dto.CreateReviewRequest;
import com.gamegearmatch.backend.review.dto.ReviewResponse;
import com.gamegearmatch.backend.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(authentication.getName(), productId, request));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }
}
