package com.flash.order.infrastructure.messaging.topic;

import lombok.Getter;

@Getter
public enum KafkaTopic {
    ORDER_PAYMENT("order-payment"),
    PRODUCT_STOCK_DECREASE("product-stock-decrease"),
    FLASH_PRODUCT_STOCK_DECREASE("flash-product-stock-decrease");

    private final String topic;

    KafkaTopic(String topic) {
        this.topic = topic;
    }
}
