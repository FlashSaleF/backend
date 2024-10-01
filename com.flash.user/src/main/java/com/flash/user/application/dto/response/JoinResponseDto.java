package com.flash.user.application.dto.response;

import lombok.Builder;

@Builder
public record JoinResponseDto(
        Long id,
        String email,
        String role,
        String name
) {
}

