package com.flash.alarm.application.dto.response;

import jakarta.validation.constraints.NotBlank;

public record UserResponseDto(
        String name,
        @NotBlank String email,
        @NotBlank Long id
) {
}
