package com.flash.flashsale.application.dto.response;

import java.util.UUID;

public record ProductStockDecreaseResponseDto(
        UUID productId,
        int status
) {
}