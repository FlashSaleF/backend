package com.flash.vendor.application.dto.response;

import java.util.UUID;

public record ProductStockIncreaseResponseDto(
        UUID productId,
        int status
) {
}
