package com.flash.vendor.infrastructure.messaging.event;

import com.flash.vendor.domain.model.ProductStatus;

import java.util.Optional;
import java.util.UUID;


public record ProductUpdateEvent(
        UUID productId,
        Optional<String> name,
        Optional<Integer> price,
        Optional<Integer> stock,
        Optional<String> description,
        Optional<ProductStatus> status,
        Optional<Boolean> isDeleted
) {
    public ProductUpdateEvent(UUID productId, Optional<String> name, Optional<Integer> price, Optional<Integer> stock, Optional<String> description, Optional<ProductStatus> status, Optional<Boolean> isDeleted) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.status = status;
        this.isDeleted = isDeleted;
    }

    public ProductUpdateEvent(UUID productId, ProductStatus status) {
        this(productId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(status), Optional.empty());
    }

    public ProductUpdateEvent(UUID productId, Boolean isDeleted) {
        this(productId, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(isDeleted));
    }
}
