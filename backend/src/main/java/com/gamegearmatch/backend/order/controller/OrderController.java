package com.gamegearmatch.backend.order.controller;

import com.gamegearmatch.backend.order.dto.OrderResponse;
import com.gamegearmatch.backend.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrders(authentication.getName()));
    }

    @PostMapping("/{paymentOrderId}/cancel")
    public ResponseEntity<OrderResponse> cancel(
            Authentication authentication, @PathVariable String paymentOrderId) {
        return ResponseEntity.ok(orderService.cancelPendingOrder(authentication.getName(), paymentOrderId));
    }
}
