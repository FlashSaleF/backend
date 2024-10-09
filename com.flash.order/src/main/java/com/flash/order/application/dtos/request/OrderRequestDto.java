package com.flash.order.application.dtos.request;

import java.util.List;

public record OrderRequestDto(
        List<OrderProductDto> orderProducts,  // 주문 상품 목록
        String address  // 배송지 주소
) {
}
