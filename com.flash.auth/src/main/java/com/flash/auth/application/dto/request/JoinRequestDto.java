package com.flash.auth.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JoinRequestDto(
        @NotBlank @Email(message = "이메일 형식에 맞게 입력해 주세요.")
        String email,

        @NotBlank @Size(min = 6, max = 20)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=[\\\\]{};':\"\\\\\\\\|,.<>/?]).+$",
                message = "길이에 맞게 영어 대소문자, 숫자, 특수문자를 포함해주세요.")
        String password,

        @NotBlank
        String role,

        String address,

        @NotBlank @Size(min = 13, max = 13)
        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}",
                message = "000-1234-1234 형식에 맞게 입력해주세요")
        String phone,

        @NotBlank
        String name
) {
}
