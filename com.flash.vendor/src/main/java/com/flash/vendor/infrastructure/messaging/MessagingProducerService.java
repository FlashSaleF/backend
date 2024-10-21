package com.flash.vendor.infrastructure.messaging;

import com.flash.vendor.application.dto.ProductSnapshot;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.infrastructure.messaging.event.ProductUpdateEvent;
import com.flash.vendor.infrastructure.messaging.serialization.EventSerializer;
import com.flash.vendor.infrastructure.messaging.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventSerializer eventSerializer;

    @Retryable(
            value = {KafkaException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void sendProductUpdateEvent(ProductSnapshot existingProduct, Product newProduct) {

        String eventData = eventSerializer.serialize(new ProductUpdateEvent(
                existingProduct.id(),
                getUpdatedFieldIfChanged(existingProduct.name(), newProduct.getName()),
                getUpdatedFieldIfChanged(existingProduct.price(), newProduct.getPrice()),
                getUpdatedFieldIfChanged(existingProduct.stock(), newProduct.getStock()),
                getUpdatedFieldIfChanged(existingProduct.description(), newProduct.getDescription()),
                getUpdatedFieldIfChanged(existingProduct.status(), newProduct.getStatus()),
                getUpdatedFieldIfChanged(existingProduct.isDeleted(), newProduct.isDeleted())
        ));

        kafkaTemplate.send(KafkaTopic.PRODUCT_UPDATE.getTopic(), eventData);
    }

    @Retryable(
            value = {KafkaException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void sendProductUpdateEventByStatusField(
            UUID productId, ProductStatus newStatus
    ) {

        String eventData = eventSerializer.serialize(
                new ProductUpdateEvent(productId, newStatus));

        kafkaTemplate.send(KafkaTopic.PRODUCT_UPDATE.getTopic(), eventData);

    }

    @Retryable(
            value = {KafkaException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000))
    public void sendProductUpdateEventByIsDeletedField(UUID productId) {

        String eventData = eventSerializer.serialize(
                new ProductUpdateEvent(productId, true));

        kafkaTemplate.send(KafkaTopic.PRODUCT_UPDATE.getTopic(), eventData);

    }

    private <T> Optional<T> getUpdatedFieldIfChanged(T existingValue, T newValue) {
        if ((existingValue == null && newValue != null) ||
                (existingValue != null && !existingValue.equals(newValue))) {
            return Optional.ofNullable(newValue);
        } else {
            return Optional.empty();
        }
    }
}
