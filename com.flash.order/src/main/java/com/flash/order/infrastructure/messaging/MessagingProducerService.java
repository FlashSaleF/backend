package com.flash.order.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.request.ProductStockIncreaseRequestDto;
import com.flash.order.domain.model.Order;
import com.flash.order.infrastructure.messaging.event.*;
import com.flash.order.infrastructure.messaging.topic.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendDecreaseProductStock(UUID orderId, UUID productId, ProductStockDecreaseRequestDto request) {
        ProductStockDecreaseEvent event = new ProductStockDecreaseEvent(orderId, productId, request);

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopic.PRODUCT_STOCK_DECREASE.getTopic(), productId.toString(), eventJson);  //productId를 파티셔닝 키로 설정 -> 동일 상품에 대해선 순차적으로 처리시키도록
        } catch (JsonProcessingException e) {
            log.error("상품 재고 감소 요청 메시지를 직렬화 중 오류 발생", e);
        }
    }

    public void sendDecreaseFlashProductStock(UUID orderId, UUID flashSaleProductId, ProductStockDecreaseRequestDto request) {
        FlashProductStockDecreaseEvent event = new FlashProductStockDecreaseEvent(orderId, flashSaleProductId, request);

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopic.FLASH_PRODUCT_STOCK_DECREASE.getTopic(), flashSaleProductId.toString(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("플래시 세일 상품 재고 감소 요청 메시지를 직렬화 중 오류 발생", e);
        }
    }

    public void sendIncreaseProductStock(UUID orderId, UUID productId, ProductStockIncreaseRequestDto request) {
        ProductStockIncreaseEvent event = new ProductStockIncreaseEvent(orderId, productId, request);

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopic.PRODUCT_STOCK_INCREASE.getTopic(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("상품 재고 증가 요청 메시지를 직렬화 중 오류 발생", e);
        }
    }

    public void sendIncreaseFlashProductStock(UUID orderId, UUID flashSaleProductId, ProductStockIncreaseRequestDto request) {
        FlashProductStockIncreaseEvent event = new FlashProductStockIncreaseEvent(orderId, flashSaleProductId, request);

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopic.FLASH_PRODUCT_STOCK_INCREASE.getTopic(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("플래시 세일 상품 재고 증가 요청 메시지를 직렬화 중 오류 발생", e);
        }
    }

    public void sendPaymentRequest(Order order) {
        // 주문 결제 요청 이벤트 생성
        OrderPaymentEvent event = new OrderPaymentEvent(order.getId(), order.getPayment().getStatus(), order.getTotalPrice());

        try {
            // 주문 결제 요청 이벤트 직렬화
            String eventJson = objectMapper.writeValueAsString(event);
            // 주문 결제 요청 이벤트 발행
            kafkaTemplate.send(KafkaTopic.ORDER_PAYMENT.getTopic(), eventJson);
        } catch (JsonProcessingException e) {
            log.error("결제 요청 메시지를 직렬화 중 오류 발생", e);
        }
    }
}
