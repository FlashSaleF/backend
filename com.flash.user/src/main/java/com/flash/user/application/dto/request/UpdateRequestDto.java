package com.flash.user.application.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRequestDto(
        String address,
        @Size(min = 13, max = 13)
        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}",
                message = "000-1234-1234 형식에 맞게 입력해주세요")
        String phone,
        String name
) {
}
