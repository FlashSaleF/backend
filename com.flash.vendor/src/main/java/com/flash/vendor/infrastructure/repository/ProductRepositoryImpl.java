package com.flash.vendor.infrastructure.repository;

import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Product findByIdAndIsDeletedFalse(UUID productId) {
        return jpaProductRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 상품이 없습니다."));
    }

    @Override
    public Page<Product> findAllByIsDeletedFalse(Pageable pageable) {
        return jpaProductRepository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    public Page<Product> searchProductsByFilters(
            String name, Integer lprice, Integer hprice, ProductStatus status, Pageable pageable
    ) {
        return jpaProductRepository.searchProducts(name, lprice, hprice, status, pageable);
    }
}
