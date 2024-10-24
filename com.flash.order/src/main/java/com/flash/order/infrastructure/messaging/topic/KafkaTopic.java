package com.flash.order.infrastructure.messaging.topic;

import lombok.Getter;

@Getter
public enum KafkaTopic {
    ORDER_PAYMENT("order-payment"),
    PRODUCT_STOCK_DECREASE("product-stock-decrease"),
    PRODUCT_STOCK_INCREASE("product-stock-increase"),
    FLASH_PRODUCT_STOCK_DECREASE("flash-product-stock-decrease"),
    FLASH_PRODUCT_STOCK_INCREASE("flash-product-stock-increase"),
    CANCELL_PAYMENT("cancel-payment");

    private final String topic;

    KafkaTopic(String topic) {
        this.topic = topic;
    }
}
