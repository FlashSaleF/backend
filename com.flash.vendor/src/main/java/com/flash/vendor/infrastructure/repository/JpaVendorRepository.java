package com.flash.vendor.infrastructure.repository;

import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.VendorRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaVendorRepository extends JpaRepository<Vendor, UUID>, VendorRepository {

    Optional<Vendor> findByAddressAndIsDeletedFalse(String address);

    Optional<Vendor> findByIdAndUserIdAndIsDeletedFalse(UUID vendorId, Long userId);

    Optional<Vendor> findByIdAndIsDeletedFalse(UUID vendorId);
}
