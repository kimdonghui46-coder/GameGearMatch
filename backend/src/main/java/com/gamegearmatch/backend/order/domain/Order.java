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

    @Column(length = 50)
    private String recipientName;

    @Column(length = 30)
    private String recipientPhone;

    @Column(length = 10)
    private String postalCode;

    @Column(length = 255)
    private String address;

    @Column(length = 255)
    private String deliveryRequest;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, String recipientName, String recipientPhone, String postalCode,
                 String address, String deliveryRequest) {
        this.user = user;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.postalCode = postalCode;
        this.address = address;
        this.deliveryRequest = deliveryRequest;
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

    public void cancelOrRefund() {
        if (this.status == OrderStatus.SHIPPING || this.status == OrderStatus.DELIVERED
                || this.status == OrderStatus.CANCELLED_REFUNDED) {
            throw new IllegalStateException("배송이 시작되었거나 이미 취소된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELLED_REFUNDED;
    }

    public void updateStatus(OrderStatus nextStatus) {
        boolean valid = (status == OrderStatus.PAID && nextStatus == OrderStatus.PREPARING)
                || (status == OrderStatus.PREPARING && nextStatus == OrderStatus.SHIPPING)
                || (status == OrderStatus.SHIPPING && nextStatus == OrderStatus.DELIVERED);
        if (!valid) throw new IllegalStateException("현재 주문 상태에서 해당 상태로 변경할 수 없습니다.");
        this.status = nextStatus;
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
