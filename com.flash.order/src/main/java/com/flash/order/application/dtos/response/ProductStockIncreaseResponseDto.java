package com.flash.order.application.dtos.response;

import java.util.UUID;

public record ProductStockIncreaseResponseDto(
        UUID productId,
        int status
) {
}
