package com.flash.user.application.dto.response;

import lombok.Builder;

@Builder
public record UserInfoResponseDto(
        String email,
        String role,
        String address,
        String phone,
        String name
) {
}
