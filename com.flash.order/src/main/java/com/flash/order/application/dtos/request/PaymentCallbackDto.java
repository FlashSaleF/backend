package com.flash.order.application.dtos.request;

import java.util.UUID;

public record PaymentCallbackDto(
        String paymentUid,
        UUID orderId
) {
}
