package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Optional<Product> findByIdAndIsDeletedFalse(UUID productId);
}
