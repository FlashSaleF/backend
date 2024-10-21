package com.flash.vendor.infrastructure.messaging.topic;

import lombok.Getter;

@Getter
public enum KafkaTopic {
    PRODUCT_UPDATE("product-update");

    private final String topic;

    KafkaTopic(String topic) {
        this.topic = topic;
    }
}
