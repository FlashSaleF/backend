package com.flash.user.application.dto.response;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String id,
        String role
) {
}
