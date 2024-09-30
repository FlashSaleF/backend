package com.flash.order.application.dtos;

import com.flash.order.presentation.dtos.OrderProductDto;

import java.util.List;

public record OrderResponseDto(
        String orderId,  // 주문 ID
        String userId,  // 사용자 ID
        List<OrderProductDto> orderProducts,  // 주문 상품 목록
        String address,  // 배송지 주소
        String orderStatus,  // 주문 상태
        int totalPrice,  // 총 가격
        String paymentId  // 결제 ID
) {
}
