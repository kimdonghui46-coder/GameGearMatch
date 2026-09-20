package com.gamegearmatch.backend.order.repository;

import com.gamegearmatch.backend.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.gamegearmatch.backend.order.domain.OrderStatus;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrderId(Long orderId);
    boolean existsByOrderUserEmailAndProductIdAndOrderStatus(
            String email, Long productId, OrderStatus status
    );
}
