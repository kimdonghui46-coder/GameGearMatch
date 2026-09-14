package com.gamegearmatch.backend.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_specs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true
    )
    private Product product;

    private Integer weight;

    private Integer dpi;

    private Integer pollingRate;

    private Integer buttonCount;

    @Column(length = 50)
    private String switchType;

    @Column(length = 50)
    private String keyboardLayout;

    private Integer noiseLevel;

    private Integer responseTime;

    private Integer batteryHours;

    private Boolean microphone;

    @Builder
    public ProductSpec(
            Product product,
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
        this.product = product;
        this.weight = weight;
        this.dpi = dpi;
        this.pollingRate = pollingRate;
        this.buttonCount = buttonCount;
        this.switchType = switchType;
        this.keyboardLayout = keyboardLayout;
        this.noiseLevel = noiseLevel;
        this.responseTime = responseTime;
        this.batteryHours = batteryHours;
        this.microphone = microphone;
    }

    public void update(
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
        this.weight = weight;
        this.dpi = dpi;
        this.pollingRate = pollingRate;
        this.buttonCount = buttonCount;
        this.switchType = switchType;
        this.keyboardLayout = keyboardLayout;
        this.noiseLevel = noiseLevel;
        this.responseTime = responseTime;
        this.batteryHours = batteryHours;
        this.microphone = microphone;
    }
}