package com.flash.order.application.dtos.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlashSaleProductResponseDto(
        UUID flashSaleProductId,
        UUID productId,
        Integer salePrice,
        Integer stock,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        FlashSaleResponseDto flashSale
) {
    public UUID getProductId() {
        return this.productId;
    }
}
