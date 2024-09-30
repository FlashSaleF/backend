package com.flash.order.presentation.dtos;

import java.util.List;

public record OrderRequestDto(
        String userId,  // 사용자 ID
        List<OrderProductDto> orderProducts,  // 주문 상품 목록
        String address,  // 배송지 주소
        int totalPrice   // 총 가격
) {
}
