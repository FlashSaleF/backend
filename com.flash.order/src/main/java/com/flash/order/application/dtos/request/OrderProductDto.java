package com.flash.order.application.dtos.request;

import lombok.Getter;

import java.util.UUID;

public record OrderProductDto(
        UUID productId,  // 상품 ID
        int quantity,  // 수량
        int price  // 가격
) {
}