package com.flash.order.application.dtos.response;

import java.util.Date;

public record RefundResponseDto(
        String paymentUid,    // 결제 고유 ID
        String status,        // 환불 상태
        int refundAmount,     // 환불된 금액
        Date refundedAt       // 환불 일시
) {
}
