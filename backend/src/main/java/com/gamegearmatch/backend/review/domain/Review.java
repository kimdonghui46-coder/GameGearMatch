package com.gamegearmatch.backend.review.domain;

import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(
        name = "uk_review_user_product", columnNames = {"user_id", "product_id"}
))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Review(User user, Product product, Integer rating, String content) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.content = content;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
