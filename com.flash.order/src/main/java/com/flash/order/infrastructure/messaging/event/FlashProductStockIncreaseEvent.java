package com.flash.order.infrastructure.messaging.event;

import com.flash.order.application.dtos.request.ProductStockIncreaseRequestDto;

import java.util.UUID;

public record FlashProductStockIncreaseEvent(
        UUID orderId,
        UUID flashSaleProductId,
        ProductStockIncreaseRequestDto request // 증가시킬 수량
) {
}
