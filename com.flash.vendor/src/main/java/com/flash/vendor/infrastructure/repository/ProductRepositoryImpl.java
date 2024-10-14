package com.flash.vendor.infrastructure.repository;

import com.flash.base.exception.CustomException;
import com.flash.vendor.domain.exception.ProductErrorCode;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
                new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
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

    @Override
    public List<Product> findAllById(List<UUID> productIds) {
        return jpaProductRepository.findAllById(productIds);
    }
}
