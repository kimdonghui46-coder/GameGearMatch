package com.gamegearmatch.backend.product.service;

import com.gamegearmatch.backend.product.domain.Product;
import com.gamegearmatch.backend.product.domain.ProductCategory;
import com.gamegearmatch.backend.product.domain.ProductSpec;
import com.gamegearmatch.backend.product.dto.ProductCreateRequest;
import com.gamegearmatch.backend.product.dto.ProductResponse;
import com.gamegearmatch.backend.product.dto.ProductSpecRequest;
import com.gamegearmatch.backend.product.repository.ProductRepository;
import com.gamegearmatch.backend.product.repository.ProductSpecRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;

    @Transactional
    public ProductResponse createProduct(
            ProductCreateRequest request
    ) {
        Product product = Product.builder()
                .name(request.name().trim())
                .category(request.category())
                .brand(request.brand())
                .price(request.price())
                .stock(request.stock())
                .connectionType(request.connectionType())
                .imageUrl(request.imageUrl())
                .description(request.description())
                .build();

        Product savedProduct =
                productRepository.save(product);

        ProductSpecRequest specRequest = request.spec();

        ProductSpec productSpec = ProductSpec.builder()
                .product(savedProduct)
                .weight(specRequest.weight())
                .dpi(specRequest.dpi())
                .pollingRate(specRequest.pollingRate())
                .buttonCount(specRequest.buttonCount())
                .switchType(specRequest.switchType())
                .keyboardLayout(specRequest.keyboardLayout())
                .noiseLevel(specRequest.noiseLevel())
                .responseTime(specRequest.responseTime())
                .batteryHours(specRequest.batteryHours())
                .microphone(specRequest.microphone())
                .build();

        ProductSpec savedSpec =
                productSpecRepository.save(productSpec);

        return ProductResponse.from(
                savedProduct,
                savedSpec
        );
    }

    public List<ProductResponse> getProducts(
            ProductCategory category
    ) {
        List<Product> products;

        if (category == null) {
            products = productRepository.findAll();
        } else {
            products =
                    productRepository.findByCategory(category);
        }

        return products.stream()
                .map(product -> {
                    ProductSpec spec =
                            getProductSpec(product.getId());

                    return ProductResponse.from(
                            product,
                            spec
                    );
                })
                .toList();
    }

    public ProductResponse getProduct(Long productId) {
        Product product = findProduct(productId);
        ProductSpec spec = getProductSpec(productId);

        return ProductResponse.from(product, spec);
    }

    @Transactional
    public ProductResponse updateProduct(
            Long productId,
            ProductCreateRequest request
    ) {
        Product product = findProduct(productId);
        ProductSpec productSpec =
                getProductSpec(productId);

        product.update(
                request.name().trim(),
                request.category(),
                request.brand(),
                request.price(),
                request.stock(),
                request.connectionType(),
                request.imageUrl(),
                request.description()
        );

        ProductSpecRequest specRequest = request.spec();

        productSpec.update(
                specRequest.weight(),
                specRequest.dpi(),
                specRequest.pollingRate(),
                specRequest.buttonCount(),
                specRequest.switchType(),
                specRequest.keyboardLayout(),
                specRequest.noiseLevel(),
                specRequest.responseTime(),
                specRequest.batteryHours(),
                specRequest.microphone()
        );

        return ProductResponse.from(
                product,
                productSpec
        );
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);

        productSpecRepository.deleteByProduct_Id(productId);
        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상품을 찾을 수 없습니다."
                        )
                );
    }

    private ProductSpec getProductSpec(Long productId) {
        return productSpecRepository
                .findByProduct_Id(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상품 사양을 찾을 수 없습니다."
                        )
                );
    }
}