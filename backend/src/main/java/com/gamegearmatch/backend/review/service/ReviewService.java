package com.gamegearmatch.backend.review.service;

import com.gamegearmatch.backend.order.domain.OrderStatus;
import com.gamegearmatch.backend.order.repository.OrderItemRepository;
import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.review.domain.Review;
import com.gamegearmatch.backend.review.dto.CreateReviewRequest;
import com.gamegearmatch.backend.review.dto.ReviewResponse;
import com.gamegearmatch.backend.review.repository.ReviewRepository;
import com.gamegearmatch.backend.user.domain.User;
import com.gamegearmatch.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponse createReview(String email, Long productId, CreateReviewRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        if (!orderItemRepository.existsByOrderUserEmailAndProductIdAndOrderStatus(
                email, productId, OrderStatus.PAID)) {
            throw new IllegalArgumentException("구매한 상품만 리뷰를 작성할 수 있습니다.");
        }
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new IllegalArgumentException("이미 리뷰를 작성한 상품입니다.");
        }
        Review review = reviewRepository.save(Review.builder()
                .user(user).product(product).rating(request.rating())
                .content(request.content().trim()).build());
        return ReviewResponse.from(review);
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다.");
        }
        return reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(ReviewResponse::from).toList();
    }
}
