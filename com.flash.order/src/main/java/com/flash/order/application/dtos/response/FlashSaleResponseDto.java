package com.flash.order.application.dtos.response;

import java.time.LocalDate;
import java.util.UUID;

public record FlashSaleResponseDto(
        UUID id,
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
