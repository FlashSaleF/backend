package com.flash.vendor.application.dto;

import com.flash.vendor.domain.model.ProductStatus;

import java.util.UUID;

public record ProductSnapshot(
        UUID id,
        String name,
        Integer price,
        Integer stock,
        ProductStatus status,
        String description,
        Boolean isDeleted
) {
}
