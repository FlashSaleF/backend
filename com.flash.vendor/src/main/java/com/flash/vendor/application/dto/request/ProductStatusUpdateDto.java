
package com.flash.vendor.application.dto.request;

import com.flash.vendor.domain.model.ProductStatus;

public record ProductStatusUpdateDto(
        ProductStatus status
) {
}
