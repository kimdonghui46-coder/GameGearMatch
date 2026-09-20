package com.gamegearmatch.backend.product.repository;

import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);

    boolean existsByName(String name);

    Optional<Product> findByName(String name);
}
