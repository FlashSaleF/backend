package com.flash.vendor.infrastructure.repository;

import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

    Optional<Product> findByIdAndIsDeletedFalse(UUID productId);
}
