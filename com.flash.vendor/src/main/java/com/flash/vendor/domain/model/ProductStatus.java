package com.flash.vendor.domain.model;

import com.flash.base.exception.CustomException;
import com.flash.vendor.domain.exception.ProductErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        log.warn("UNKNOWN_PRODUCT_STATUS : {}", value);
        throw new CustomException(ProductErrorCode.UNKNOWN_PRODUCT_STATUS);
    }
}
