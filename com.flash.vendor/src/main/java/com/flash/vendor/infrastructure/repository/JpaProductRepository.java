package com.flash.vendor.infrastructure.repository;

import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndIsDeletedFalse(UUID productId);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE "
            + "(:name IS NULL OR p.name LIKE %:name%) AND "
            + "(:lprice IS NULL OR p.price >= :lprice) AND "
            + "(:hprice IS NULL OR p.price <= :hprice) AND "
            + "(:status IS NULL OR p.status = :status)")
    Page<Product> searchProducts(
            @Param("name") String name,
            @Param("lprice") Integer lprice,
            @Param("hprice") Integer hprice,
            @Param("status") ProductStatus status,
            Pageable pageable
    );
}
