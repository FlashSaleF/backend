package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository {

    Optional<Vendor> findByAddress(String address);

    Vendor save(Vendor vendor);

    Optional<Vendor> findById(UUID vendorId);

    Page<Vendor> findAll(Pageable pageable);

    Optional<Vendor> findByIdAndUserId(UUID vendorId, Long userId);
}
