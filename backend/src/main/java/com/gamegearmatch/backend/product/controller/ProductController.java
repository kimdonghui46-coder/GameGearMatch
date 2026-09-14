package com.gamegearmatch.backend.product.controller;

import com.gamegearmatch.backend.product.domain.ProductCategory;
import com.gamegearmatch.backend.product.dto.ProductResponse;
import com.gamegearmatch.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false)
            ProductCategory category
    ) {
        List<ProductResponse> response =
                productService.getProducts(category);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long productId
    ) {
        ProductResponse response =
                productService.getProduct(productId);

        return ResponseEntity.ok(response);
    }
}