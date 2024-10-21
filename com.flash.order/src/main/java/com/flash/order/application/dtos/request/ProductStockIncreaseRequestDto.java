package com.flash.order.application.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductStockIncreaseRequestDto(
        @NotNull(message = "요청 수량은 필수 입력 항목입니다.")
        @Min(value = 1, message = "요청 수량은 1 이상이어야 합니다.")
        Integer quantity
) {
}
