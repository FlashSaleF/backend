package com.flash.order.application.dtos.response;

import java.time.LocalDateTime;
import java.util.Date;

public record PaymentResponseDto(
        String paymentUid,       // 결제 고유 ID
        String status,            // 결제 상태
        int totalPrice,        // 결제 금액
        String merchantUid,       // 주문 고유 ID
        String buyerName,         // 구매자 이름
        String buyerTel,          // 구매자 전화번호
        String buyerEmail,        // 구매자 이메일
        String payMethod,         // 결제 방식
        Date paidAt,     // 결제 일시
        String maskedCardNumber,  // 마스킹된 카드 번호
        String cardName,          // 카드 이름
        String receiptUrl        // 영수증 URL
) {
}
