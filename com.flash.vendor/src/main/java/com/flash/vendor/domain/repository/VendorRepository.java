package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository {

    Optional<Vendor> findByAddressIsDeletedFalse(String address);

    Vendor save(Vendor vendor);

    Page<Vendor> findAll(Pageable pageable);

    Optional<Vendor> findByIdAndUserIdIsDeletedFalse(UUID vendorId, Long userId);

    Optional<Vendor> findByIdAndIsDeletedFalse(UUID vendorId);
}
