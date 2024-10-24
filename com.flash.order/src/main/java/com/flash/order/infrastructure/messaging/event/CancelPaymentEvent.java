package com.flash.order.infrastructure.messaging.event;

import java.util.UUID;

public record CancelPaymentEvent(
        UUID orderId
) {
}
