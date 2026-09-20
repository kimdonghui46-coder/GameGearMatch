package com.gamegearmatch.backend.order.domain;

import com.gamegearmatch.backend.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(unique = true, length = 50)
    private String paymentOrderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user) {
        this.user = user;
        this.totalPrice = 0;
        this.status = OrderStatus.PENDING;
        this.paymentOrderId = "GGM-" + UUID.randomUUID();
    }

    public void addItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.assignOrder(this);
        recalculateTotalPrice();
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 대기 주문만 취소할 수 있습니다.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public String getOrderName() {
        if (orderItems.isEmpty()) return "GameGearMatch 상품";
        String firstName = orderItems.getFirst().getProduct().getName();
        return orderItems.size() == 1 ? firstName : firstName + " 외 " + (orderItems.size() - 1) + "건";
    }

    private void recalculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getLineTotal)
                .sum();
    }

    @PrePersist
    public void prePersist() {
        if (totalPrice == null) {
            totalPrice = 0;
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        createdAt = LocalDateTime.now();
    }
}
