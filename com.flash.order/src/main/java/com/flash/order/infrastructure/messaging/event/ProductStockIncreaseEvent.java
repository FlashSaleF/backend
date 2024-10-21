package com.flash.order.infrastructure.messaging.event;

import com.flash.order.application.dtos.request.ProductStockIncreaseRequestDto;

import java.util.UUID;

public record ProductStockIncreaseEvent(
        UUID orderId,
        UUID productId,
        ProductStockIncreaseRequestDto request // 증가시킬 수량
) {
}
