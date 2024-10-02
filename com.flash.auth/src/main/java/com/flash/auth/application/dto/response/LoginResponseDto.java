package com.flash.auth.application.dto.response;

import lombok.Builder;
import lombok.With;

@Builder
public record LoginResponseDto(
        String id,
        String role,
        @With String accessToken,
        @With String refreshToken
) {
}
