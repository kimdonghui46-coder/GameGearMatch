package com.gamegearmatch.backend.payment.domain;

import com.gamegearmatch.backend.order.domain.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id", nullable = false, unique = true) private Order order;
    @Column(nullable = false, unique = true, length = 200) private String paymentKey;
    @Column(nullable = false) private Integer amount;
    @Column(length = 50) private String method;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false) private LocalDateTime approvedAt;

    @Builder
    public Payment(Order order, String paymentKey, Integer amount, String method, String status) {
        this.order = order; this.paymentKey = paymentKey; this.amount = amount;
        this.method = method; this.status = status; this.approvedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        this.status = "CANCELED";
    }
}
