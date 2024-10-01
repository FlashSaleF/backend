package com.flash.flashsale.application.dto.request;

import java.time.LocalDate;

public record FlashSaleRequestDto(
    String name,
    LocalDate startDate,
    LocalDate endDate
) {
}