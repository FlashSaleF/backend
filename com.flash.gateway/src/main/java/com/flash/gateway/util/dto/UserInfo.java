package com.flash.gateway.util.dto;

import lombok.Builder;

@Builder
public record UserInfo(
        String id,
        String role,
        String accessToken
) {
}
