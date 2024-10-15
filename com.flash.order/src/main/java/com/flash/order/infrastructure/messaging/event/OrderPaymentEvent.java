package com.flash.order.infrastructure.messaging.event;

import com.flash.order.domain.model.PaymentStatus;

import java.util.UUID;

public record OrderPaymentEvent(
        UUID orderId,          // 주문 ID
        PaymentStatus paymentStatus,  // 결제 상태 (성공, 실패 등)
        int paymentAmount
) {
}
