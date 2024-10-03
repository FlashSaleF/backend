package com.flash.order.application.dtos.request;

public record PaymentRequestDto(
        String paymentUid,
        String orderUid
) {
}
