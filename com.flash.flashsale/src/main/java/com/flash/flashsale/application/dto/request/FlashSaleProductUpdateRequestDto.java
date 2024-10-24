package com.flash.flashsale.application.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record FlashSaleProductUpdateRequestDto(
    @NotNull(message = "세일 가격 필수 값입니다.")
    @Positive(message = "세일 가격은 양수가 필요합니다.")
    Integer salePrice,
    @NotNull(message = "시작 시간은 필수 값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH")
    @FutureOrPresent(message = "과거 시간은 넣을 수 없습니다.")
    LocalDateTime startTime,
    @NotNull(message = "종료 시간 필수 값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH")
    @FutureOrPresent(message = "과거 시간은 넣을 수 없습니다.")
    LocalDateTime endTime
) {
}
