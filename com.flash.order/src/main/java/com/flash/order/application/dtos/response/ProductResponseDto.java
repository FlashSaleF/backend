package com.flash.order.application.dtos.response;

import com.flash.order.domain.model.ProductStatus;

import java.util.Optional;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String name,
        Integer price,
        Integer stock,
        ProductStatus status,
        String description,
        UUID vendorId,
        Optional<FlashSaleProductResponseDto> flashSaleProductResponseDto
) {
    public ProductResponseDto(UUID id, String name, Integer price, Integer stock, ProductStatus status, String description, UUID vendorId) {
        this(id, name, price, stock, status, description, vendorId, Optional.empty());
    }

    public ProductResponseDto(UUID id, String name, Integer price, Integer stock, ProductStatus status, String description, UUID vendorId, FlashSaleProductResponseDto flashSaleProductResponseDto) {
        this(id, name, price, stock, status, description, vendorId, Optional.ofNullable(flashSaleProductResponseDto));
    }
}
