package com.flash.vendor.domain.model;

public enum ProductStatus {
    AVAILABLE,
    ON_SALE,
    OUT_OF_STOCK;

    public static ProductStatus fromString(String value) {
        for (ProductStatus status : ProductStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown product status: " + value);
    }
}
