package com.flash.order.application.dtos.mapper;

import com.flash.order.application.dtos.response.PaymentDetailsResponseDto;
import com.flash.order.application.dtos.response.PaymentResponseDto;
import com.flash.order.application.dtos.response.RefundResponseDto;
import com.siot.IamportRestClient.response.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentDetailsResponseDto convertToDetailsResponseDto(Payment payment) {

        return new PaymentDetailsResponseDto(
                payment.getImpUid(),      // 결제 고유 ID
                payment.getStatus(),   // 결제 상태
                payment.getAmount().intValue(),           // 결제 금액
                payment.getMerchantUid(),     // 주문 고유 ID
                payment.getBuyerName(),       // 구매자 이름
                payment.getBuyerTel(),        // 구매자 전화번호
                payment.getBuyerEmail(),      // 구매자 이메일
                payment.getPayMethod(),       // 결제 방식
                payment.getPaidAt(),          // 결제 일시
                payment.getCardNumber(),             // 마스킹된 카드 번호
                payment.getCardName(),        // 카드 이름
                payment.getReceiptUrl()       // 영수증 URL
        );
    }

    public RefundResponseDto convertToRefundResponseDto(Payment payment) {
        return new RefundResponseDto(
                payment.getImpUid(),
                payment.getStatus(),
                payment.getAmount().intValue(),
                payment.getCancelledAt() // 환불 시점을 현재 시간으로 설정
        );
    }

    public PaymentResponseDto convertToResponseDto(com.flash.order.domain.model.Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getUserId(),
                payment.getStatus().name(),
                payment.getPrice(),
                payment.getOrder().getId()
        );
    }

    // 카드 번호 마스킹 처리 로직
//    private String maskCardNumber(String cardNumber) {
//        if (cardNumber == null || cardNumber.length() < 10) {
//            return cardNumber;
//        }
//        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(cardNumber.length() - 4);
//    }
}
