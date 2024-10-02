package com.flash.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank @Email(message = "이메일 형식에 맞게 입력해주세요.")
        String email,

        @NotBlank @Size(min = 6, max = 20)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\\\]{};':\"\\\\\\\\|,.<>/?]).+$",
                message = "길이에 맞게 영어 대소문자, 숫자, 특수문자를 포함해주세요.")
        String password
) {
}
