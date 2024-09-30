package com.flash.order.presentation.dtos;

public record OrderProductDto(
        String productId,  // 상품 ID
        int quantity,  // 수량
        int price  // 가격
) {
}
