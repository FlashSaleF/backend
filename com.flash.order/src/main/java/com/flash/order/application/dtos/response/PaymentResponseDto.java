package com.flash.order.application.dtos.response;

import java.util.UUID;

public record PaymentResponseDto(
        UUID paymentId,
        Long userId,  // 사용자 ID
        String paymentStatus,  // 주문 상태
        int totalPrice,  // 총 가격
        UUID orderId  // 주문 ID

) {
}
