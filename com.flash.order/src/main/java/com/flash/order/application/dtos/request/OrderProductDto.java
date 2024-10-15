package com.flash.order.application.dtos.request;

import java.util.UUID;

public record OrderProductDto(
        UUID productId,  // 상품 ID
        UUID flashSaleProductId,  // 플래시 세일 상품 ID
        int quantity  // 수량
) {
}
