package com.flash.vendor.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductRequestDto(
        @NotBlank(message = "상품명은 비워둘 수 없습니다.")
        String name,
        @NotNull(message = "가격은 필수 입력 항목입니다.")
        @Min(value = 10, message = "가격은 최소 10원 이상이어야 합니다.")
        Integer price,
        @NotNull(message = "재고는 필수 입력 항목입니다.")
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        Integer stock,
        String description,
        @NotNull(message = "해당 업체는 필수 입력 항목입니다.")
        UUID vendorId
) {
}
