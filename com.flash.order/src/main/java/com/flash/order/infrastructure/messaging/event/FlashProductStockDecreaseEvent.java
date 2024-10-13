package com.flash.order.infrastructure.messaging.event;

import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;

import java.util.UUID;

public record FlashProductStockDecreaseEvent(
        UUID FlashSaleProductId,
        ProductStockDecreaseRequestDto request // 감소시킬 수량
) {
}
