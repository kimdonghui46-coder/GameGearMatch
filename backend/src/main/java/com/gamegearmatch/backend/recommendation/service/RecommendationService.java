package com.gamegearmatch.backend.recommendation.service;

import com.gamegearmatch.backend.product.domain.ConnectionType;
import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.domain.ProductSpec;
import com.gamegearmatch.backend.product.dto.ProductResponse;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.product.repository.ProductSpecRepository;
import com.gamegearmatch.backend.recommendation.domain.GameType;
import com.gamegearmatch.backend.recommendation.dto.RecommendationRequest;
import com.gamegearmatch.backend.recommendation.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;

    public List<RecommendationResponse> recommend(RecommendationRequest request) {
        List<Candidate> candidates = productRepository.findByCategory(request.category()).stream()
                .filter(product -> product.getStock() > 0)
                .filter(product -> request.maxPrice() == null || product.getPrice() <= request.maxPrice())
                .filter(product -> matchesConnection(product.getConnectionType(), request.preferredConnection()))
                .map(product -> productSpecRepository.findByProduct_Id(product.getId())
                        .map(spec -> new Candidate(product, spec)).orElse(null))
                .filter(candidate -> candidate != null)
                .toList();

        if (candidates.isEmpty()) {
            return List.of();
        }

        Range price = range(candidates.stream().mapToDouble(c -> c.product().getPrice()).toArray());
        Range weight = range(candidates.stream().mapToDouble(c -> value(c.spec().getWeight())).toArray());
        Range polling = range(candidates.stream().mapToDouble(c -> value(c.spec().getPollingRate())).toArray());
        Range dpi = range(candidates.stream().mapToDouble(c -> value(c.spec().getDpi())).toArray());
        Range buttons = range(candidates.stream().mapToDouble(c -> value(c.spec().getButtonCount())).toArray());
        Range response = range(candidates.stream().mapToDouble(c -> value(c.spec().getResponseTime())).toArray());
        Range battery = range(candidates.stream().mapToDouble(c -> value(c.spec().getBatteryHours())).toArray());
        Range noise = range(candidates.stream().mapToDouble(c -> value(c.spec().getNoiseLevel())).toArray());
        Weights weights = weightsFor(request.gameType());

        return candidates.stream().map(candidate -> {
                    ProductSpec spec = candidate.spec();
                    double priceScore = price.lowerIsBetter(candidate.product().getPrice());
                    double comfortScore = weight.lowerIsBetter(value(spec.getWeight()));
                    double performanceScore = performanceScore(candidate, polling, dpi, response, battery);
                    double featureScore = featureScore(candidate, buttons, noise, battery);
                    double score = priceScore * weights.price()
                            + performanceScore * weights.performance()
                            + comfortScore * weights.comfort()
                            + featureScore * weights.features();
                    if (request.prioritizeLightweight()) {
                        score = score * 0.85 + comfortScore * 0.15;
                    }
                    return new RecommendationResponse(
                            ProductResponse.from(candidate.product(), spec),
                            Math.round(score * 1000.0) / 10.0,
                            reasons(candidate, request, performanceScore, comfortScore)
                    );
                })
                .sorted(Comparator.comparingDouble(RecommendationResponse::score).reversed())
                .limit(5)
                .toList();
    }

    private boolean matchesConnection(ConnectionType actual, ConnectionType preferred) {
        return preferred == null || actual == preferred || actual == ConnectionType.BOTH;
    }

    private double performanceScore(Candidate c, Range polling, Range dpi, Range response, Range battery) {
        return switch (c.product().getCategory()) {
            case MOUSE -> polling.higherIsBetter(value(c.spec().getPollingRate())) * 0.6
                    + dpi.higherIsBetter(value(c.spec().getDpi())) * 0.4;
            case KEYBOARD -> polling.higherIsBetter(value(c.spec().getPollingRate())) * 0.7
                    + response.lowerIsBetter(value(c.spec().getResponseTime())) * 0.3;
            case HEADSET -> response.lowerIsBetter(value(c.spec().getResponseTime())) * 0.6
                    + battery.higherIsBetter(value(c.spec().getBatteryHours())) * 0.4;
        };
    }

    private double featureScore(Candidate c, Range buttons, Range noise, Range battery) {
        return switch (c.product().getCategory()) {
            case MOUSE -> buttons.higherIsBetter(value(c.spec().getButtonCount()));
            case KEYBOARD -> noise.lowerIsBetter(value(c.spec().getNoiseLevel())) * 0.6
                    + battery.higherIsBetter(value(c.spec().getBatteryHours())) * 0.4;
            case HEADSET -> (Boolean.TRUE.equals(c.spec().getMicrophone()) ? 0.5 : 0.0)
                    + battery.higherIsBetter(value(c.spec().getBatteryHours())) * 0.5;
        };
    }

    private List<String> reasons(Candidate c, RecommendationRequest request, double performance, double comfort) {
        List<String> reasons = new ArrayList<>();
        reasons.add(request.gameType() + " 게임 가중치 적용");
        if (request.maxPrice() != null) reasons.add("예산 " + request.maxPrice() + "원 이하");
        if (request.preferredConnection() != null) reasons.add("선호 연결 방식 충족");
        if (performance >= 0.7) reasons.add("높은 성능 점수");
        if (request.prioritizeLightweight() && comfort >= 0.7) reasons.add("가벼운 무게 우선");
        return reasons;
    }

    private Weights weightsFor(GameType gameType) {
        return switch (gameType) {
            case FPS -> new Weights(0.10, 0.45, 0.30, 0.15);
            case MOBA -> new Weights(0.20, 0.30, 0.20, 0.30);
            case MMORPG -> new Weights(0.20, 0.20, 0.15, 0.45);
            case CASUAL -> new Weights(0.40, 0.20, 0.25, 0.15);
        };
    }

    private Range range(double[] values) {
        double min = java.util.Arrays.stream(values).min().orElse(0);
        double max = java.util.Arrays.stream(values).max().orElse(0);
        return new Range(min, max);
    }

    private double value(Integer value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private record Candidate(Product product, ProductSpec spec) {}
    private record Weights(double price, double performance, double comfort, double features) {}
    private record Range(double min, double max) {
        double higherIsBetter(double value) {
            return max == min ? 1.0 : (value - min) / (max - min);
        }
        double lowerIsBetter(double value) {
            return max == min ? 1.0 : 1.0 - ((value - min) / (max - min));
        }
    }
}
