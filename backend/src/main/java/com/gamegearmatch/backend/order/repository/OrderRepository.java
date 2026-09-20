package com.gamegearmatch.backend.order.repository;

import com.gamegearmatch.backend.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Order> findByPaymentOrderIdAndUserEmail(String paymentOrderId, String email);
    List<Order> findAllByOrderByCreatedAtDesc();
}
