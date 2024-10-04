package com.flash.vendor.application.dto.response;

import java.util.List;

public record ProductListResponseDto(
        List<ProductResponseDto> productList
) {
}
