package com.flash.order.application.dtos;

import com.flash.order.presentation.dtos.OrderProductDto;

import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID orderId,  // 주문 ID
        Long userId,  // 사용자 ID
        List<OrderProductDto> orderProducts,  // 주문 상품 목록
        String address,  // 배송지 주소
        String orderStatus,  // 주문 상태
        int totalPrice,  // 총 가격
        UUID paymentId  // 결제 ID
) {
}
