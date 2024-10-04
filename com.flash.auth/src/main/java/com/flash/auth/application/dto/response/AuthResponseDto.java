package com.flash.auth.application.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(
        String id,
        String role
) {
}
