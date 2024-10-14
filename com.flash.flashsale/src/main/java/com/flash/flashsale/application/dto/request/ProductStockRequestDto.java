package com.flash.flashsale.application.dto.request;

import java.util.UUID;

public record ProductStockRequestDto(
    UUID productId,
    Integer stock
) {
}
