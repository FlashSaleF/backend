package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);

    Product findByIdAndIsDeletedFalse(UUID productId);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    Page<Product> searchProductsByFilters(String name, Integer lprice, Integer hprice, ProductStatus status, Pageable pageable);
}
