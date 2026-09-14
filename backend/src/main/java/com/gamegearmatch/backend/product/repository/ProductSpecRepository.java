package com.gamegearmatch.backend.product.repository;

import com.gamegearmatch.backend.product.domain.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSpecRepository
        extends JpaRepository<ProductSpec, Long> {

    Optional<ProductSpec> findByProduct_Id(Long productId);

    void deleteByProduct_Id(Long productId);
}