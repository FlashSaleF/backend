package com.flash.order.presentation.dtos;

import lombok.Getter;

import java.util.UUID;

public record OrderProductDto(
        UUID productId,  // 상품 ID
        int quantity,  // 수량
        int price  // 가격
) {
}
