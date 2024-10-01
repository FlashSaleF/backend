package com.flash.vendor.application.dto.response;

import java.util.UUID;

public record VendorResponseDto(
        UUID id,
        Long userId,
        String username,
        String name,
        String address
) {
}
