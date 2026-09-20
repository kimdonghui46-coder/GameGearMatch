package com.gamegearmatch.backend.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductCategory category;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ConnectionType connectionType;

    @Column(length = 500)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean visible;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Product(
            String name,
            ProductCategory category,
            String brand,
            Integer price,
            Integer stock,
            ConnectionType connectionType,
            String imageUrl,
            String description
    ) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.connectionType = connectionType;
        this.imageUrl = imageUrl;
        this.description = description;
        this.visible = true;
    }

    public void update(
            String name,
            ProductCategory category,
            String brand,
            Integer price,
            Integer stock,
            ConnectionType connectionType,
            String imageUrl,
            String description
    ) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.connectionType = connectionType;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public void decreaseStock(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("차감 수량은 1개 이상이어야 합니다.");
        }
        if (stock < quantity) {
            throw new IllegalArgumentException(name + " 상품의 재고가 부족합니다.");
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("복구 수량은 1개 이상이어야 합니다.");
        this.stock += quantity;
    }

    public boolean isVisible() {
        return !Boolean.FALSE.equals(visible);
    }

    public void hide() {
        this.visible = false;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
