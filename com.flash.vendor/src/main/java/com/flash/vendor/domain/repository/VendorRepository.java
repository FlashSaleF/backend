package com.flash.vendor.domain.repository;

import com.flash.vendor.domain.model.Vendor;

import java.util.Optional;
import java.util.UUID;

public interface VendorRepository {

    Optional<Vendor> findByAddress(String address);

    Vendor save(Vendor vendor);

    Optional<Vendor> findById(UUID vendorId);
}
