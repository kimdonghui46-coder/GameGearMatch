package com.gamegearmatch.backend.review.repository;

import com.gamegearmatch.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByProductIdOrderByCreatedAtDesc(Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
