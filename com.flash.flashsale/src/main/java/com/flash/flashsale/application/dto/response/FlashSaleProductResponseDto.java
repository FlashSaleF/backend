package com.flash.flashsale.application.dto.response;

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
    FlashSaleResponseDto flashSaleResponseDto
) {
}
