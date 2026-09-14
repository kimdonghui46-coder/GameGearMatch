package com.gamegearmatch.backend.cart.domain;

import com.gamegearmatch.backend.product.domain.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_product",
                        columnNames = {
                                "cart_id",
                                "product_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Builder
    public CartItem(
            Cart cart,
            Product product,
            Integer quantity
    ) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public void increaseQuantity(Integer amount) {
        this.quantity += amount;
    }

    public void changeQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "수량은 1개 이상이어야 합니다."
            );
        }

        this.quantity = quantity;
    }
}