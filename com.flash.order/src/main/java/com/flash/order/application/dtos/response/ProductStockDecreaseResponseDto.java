package com.flash.order.application.dtos.response;

import java.util.UUID;

public record ProductStockDecreaseResponseDto(
        UUID productId,
        int status
) {
}
