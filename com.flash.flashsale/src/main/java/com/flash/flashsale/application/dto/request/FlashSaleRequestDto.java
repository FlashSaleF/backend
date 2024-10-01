package com.flash.flashsale.application.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record FlashSaleRequestDto(
    @NotBlank(message = "플래시 세일명은 비워둘 수 없습니다.")
    String name,
    @FutureOrPresent(message = "과거 일자는 넣을 수 없습니다.")
    LocalDate startDate,
    @FutureOrPresent(message = "과거 일자는 넣을 수 없습니다.")
    LocalDate endDate
) {
}