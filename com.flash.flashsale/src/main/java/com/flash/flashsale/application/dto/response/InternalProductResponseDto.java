package com.flash.flashsale.application.dto.response;

import com.flash.flashsale.domain.model.FlashSaleProductStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InternalProductResponseDto(
    UUID flashSaleProductId,
    UUID productId,
    Integer salePrice,
    Integer stock,
    FlashSaleProductStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    FlashSaleResponseDto flashSale
) {
}
