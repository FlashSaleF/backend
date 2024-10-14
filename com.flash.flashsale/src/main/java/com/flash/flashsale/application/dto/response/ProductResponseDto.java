package com.flash.flashsale.application.dto.response;

import java.util.UUID;

public record ProductResponseDto(
    UUID id,
    String name,
    Integer price,
    Integer stock,
    String status,
    String description,
    UUID vendorId
) {
}
