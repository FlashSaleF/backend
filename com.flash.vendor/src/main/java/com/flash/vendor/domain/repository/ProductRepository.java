package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);

    Optional<Product> findById(UUID productId);

    Optional<Product> findByIdAndIsDeletedFalse(UUID productId);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);
}
