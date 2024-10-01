package com.flash.auth.application.dto.response;

public record JoinResponseDto(
        Integer id,
        String email,
        String role,
        String name
) {
}
