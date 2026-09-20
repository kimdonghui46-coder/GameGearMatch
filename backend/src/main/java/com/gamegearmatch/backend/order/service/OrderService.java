package com.gamegearmatch.backend.order.service;

import com.gamegearmatch.backend.cart.domain.Cart;
import com.gamegearmatch.backend.cart.domain.CartItem;
import com.gamegearmatch.backend.cart.repository.CartItemRepository;
import com.gamegearmatch.backend.cart.repository.CartRepository;
import com.gamegearmatch.backend.order.domain.Order;
import com.gamegearmatch.backend.order.domain.OrderItem;
import com.gamegearmatch.backend.order.dto.OrderResponse;
import com.gamegearmatch.backend.order.repository.OrderRepository;
import com.gamegearmatch.backend.user.domain.User;
import com.gamegearmatch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Cart cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다."));
        List<CartItem> cartItems = cartItemRepository.findAllByCartIdOrderByIdDesc(cart.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        Order order = Order.builder().user(user).build();
        for (CartItem cartItem : cartItems) {
            cartItem.getProduct().decreaseStock(cartItem.getQuantity());
            order.addItem(OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .orderPrice(cartItem.getProduct().getPrice())
                    .build());
        }
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllByCartId(cart.getId());
        return OrderResponse.from(savedOrder);
    }

    public List<OrderResponse> getOrders(String email) {
        return orderRepository.findAllByUserEmailOrderByCreatedAtDesc(email)
                .stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public OrderResponse cancelPendingOrder(String email, String paymentOrderId) {
        Order order = orderRepository.findByPaymentOrderIdAndUserEmail(paymentOrderId, email)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        order.getOrderItems().forEach(item -> item.getProduct().increaseStock(item.getQuantity()));
        order.cancel();
        return OrderResponse.from(order);
    }
}
