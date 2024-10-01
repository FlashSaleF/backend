package com.flash.vendor.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VendorRequestDto(
        @NotBlank(message = "회사명은 비워둘 수 없습니다.")
        String name,
        @NotBlank(message = "주소는 비워둘 수 없습니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣 ,.-]+$",
                message = "주소는 문자, 숫자, 공백, 쉼표, 마침표, 하이픈만 포함할 수 있습니다."
        )
        String address
) {
}
