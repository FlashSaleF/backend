package com.flash.flashsale.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record FlashSaleResponseDto(
    UUID id,
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}