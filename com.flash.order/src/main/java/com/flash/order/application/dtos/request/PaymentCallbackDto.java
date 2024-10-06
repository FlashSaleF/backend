package com.flash.order.application.dtos.request;

public record PaymentCallbackDto(
        String paymentUid,
        String orderUid
) {
}
