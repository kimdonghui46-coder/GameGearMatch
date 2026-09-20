package com.gamegearmatch.backend.recommendation.controller;

import com.gamegearmatch.backend.recommendation.dto.RecommendationRequest;
import com.gamegearmatch.backend.recommendation.dto.RecommendationResponse;
import com.gamegearmatch.backend.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<List<RecommendationResponse>> recommend(
            @Valid @RequestBody RecommendationRequest request
    ) {
        return ResponseEntity.ok(recommendationService.recommend(request));
    }
}
