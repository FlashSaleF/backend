package com.flash.vendor.application.dto.response;

import org.springframework.data.domain.Page;

public record VendorPageResponseDto(
        Page<VendorResponseDto> vendors
) {
}
