package com.gamegearmatch.backend.cart.service;

import com.gamegearmatch.backend.cart.domain.Cart;
import com.gamegearmatch.backend.cart.domain.CartItem;
import com.gamegearmatch.backend.cart.dto.AddCartItemRequest;
import com.gamegearmatch.backend.cart.dto.CartItemResponse;
import com.gamegearmatch.backend.cart.dto.CartResponse;
import com.gamegearmatch.backend.cart.dto.UpdateCartItemRequest;
import com.gamegearmatch.backend.cart.repository.CartItemRepository;
import com.gamegearmatch.backend.cart.repository.CartRepository;
import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.user.domain.User;
import com.gamegearmatch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse getCart(String email) {
        Cart cart = findOrCreateCart(email);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        Cart cart = findOrCreateCart(email);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> CartItem.builder().cart(cart).product(product).quantity(0).build());
        int nextQuantity = item.getQuantity() + request.quantity();
        validateStock(product, nextQuantity);
        item.changeQuantity(nextQuantity);
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = findOrCreateCart(email);
        CartItem item = findOwnedItem(cart, itemId);
        validateStock(item.getProduct(), request.quantity());
        item.changeQuantity(request.quantity());
        return toResponse(cart);
    }

    @Transactional
    public CartResponse deleteItem(String email, Long itemId) {
        Cart cart = findOrCreateCart(email);
        cartItemRepository.delete(findOwnedItem(cart, itemId));
        cartItemRepository.flush();
        return toResponse(cart);
    }

    private Cart findOrCreateCart(String email) {
        return cartRepository.findByUserEmail(email).orElseGet(() -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private CartItem findOwnedItem(Cart cart, Long itemId) {
        return cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));
    }

    private void validateStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("상품 재고가 부족합니다.");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cartItemRepository.findAllByCartIdOrderByIdDesc(cart.getId())
                .stream().map(CartItemResponse::from).toList();
        return CartResponse.of(cart.getId(), items);
    }
}
