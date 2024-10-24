package com.flash.order.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.base.exception.CustomException;
import com.flash.order.application.service.FeignClientService;
import com.flash.order.application.service.OrderService;
import com.flash.order.application.service.PaymentService;
import com.flash.order.domain.exception.OrderErrorCode;
import com.flash.order.domain.model.Order;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.infrastructure.messaging.event.*;
import com.flash.order.infrastructure.messaging.serialization.EventSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingConsumerService {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final FeignClientService feignClientService;
    private final PaymentService paymentService;

    @KafkaListener(topics = "product-stock-decrease", groupId = "order-group")
    public void listenProductStockDecreaseEvent(String message) {
        // 메시지 수신
        ProductStockDecreaseEvent event = EventSerializer.deserialize(message, ProductStockDecreaseEvent.class);

        //재고 감소 처리
        try {
            feignClientService.decreaseProductStock(event.productId(), event.request());
            // 성공 시 주문 완료 처리
            orderService.handleOrderCompleted(event.orderId());
        } catch (Exception e) {
            // 실패 시 로깅 또는 예외 처리
            log.error("Product stock decrease failed for productId: {}", event.productId(), e);
            //주문 취소 처리 -> 상태 cancelled로 변경
            orderService.cancelOrderAndPayment(event.orderId());
        }

    }

    @KafkaListener(topics = "flash-product-stock-decrease", groupId = "order-group")
    public void listenFlashProductStockDecreaseEvent(String message) {
        // 메시지 수신
        FlashProductStockDecreaseEvent event = EventSerializer.deserialize(message, FlashProductStockDecreaseEvent.class);

        //재고 감소 처리
        try {
            feignClientService.decreaseFlashSaleProductStock(event.flashSaleProductId());
            // 성공 시 주문 완료 처리
            orderService.handleOrderCompleted(event.orderId());
        } catch (Exception e) {
            // 실패 시 로깅 또는 예외 처리
            log.error("Flash Sale Product stock decrease failed for flashSaleProductId: {}", event.flashSaleProductId(), e);
            //주문 취소 처리 -> 상태 cancelled로 변경
            orderService.cancelOrderAndPayment(event.orderId());
        }

    }

    @KafkaListener(topics = "product-stock-increase", groupId = "order-group")
    public void listenProductStockIncreaseEvent(String message) {
        // 메시지 수신
        ProductStockIncreaseEvent event = EventSerializer.deserialize(message, ProductStockIncreaseEvent.class);

        //재고 증가 처리
        try {
            feignClientService.increaseProductStock(event.productId(), event.request());
        } catch (Exception e) {
            // 실패 시 로깅 또는 예외 처리
            log.error("Product stock increase failed for productId: {}", event.productId(), e);
        }

    }

    @KafkaListener(topics = "flash-product-stock-increase", groupId = "order-group")
    public void listenFlashProductStockIncreaseEvent(String message) {
        // 메시지 수신
        FlashProductStockIncreaseEvent event = EventSerializer.deserialize(message, FlashProductStockIncreaseEvent.class);

        //재고 증가 처리
        try {
            feignClientService.increaseFlashSaleProductStock(event.flashSaleProductId());
        } catch (Exception e) {
            // 실패 시 로깅 또는 예외 처리
            log.error("Flash Sale Product stock increase failed for flashSaleProductId: {}", event.flashSaleProductId(), e);
        }

    }

    @KafkaListener(topics = "order-payment", groupId = "order-group")
    public void listenOrderPaymentEvent(String message) {
        // 메시지 수신
        OrderPaymentEvent event = EventSerializer.deserialize(message, OrderPaymentEvent.class);
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.orderId())
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        orderService.handlePaymentCompleted(order.getId());
    }

    //결제 취소
    @KafkaListener(topics = "cancel-payment", groupId = "order-group")
    public void listenCancelPaymentEvent(String message) {
        // 메시지 수신
        CancelPaymentEvent event = EventSerializer.deserialize(message, CancelPaymentEvent.class);
        Order order = orderRepository.findByIdAndIsDeletedFalse(event.orderId())
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        String paymentUid = order.getPayment().getPaymentUid();

        paymentService.refundPayment(paymentUid);
    }

//    @KafkaListener(topics = "order-payment-topic")
//    public void handleOrderPaymentEvent(OrderPaymentEvent event, Order order) {
//        // 주문 조회
////        Order order = orderRepository.findOrderByOrderUid(event.getOrderUid())
////                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
//
//        // 결제 상태에 따른 주문 처리
//        if (event.paymentStatus() == PaymentStatus.completed) {
//            // 결제 성공 시, 주문 상태 변경 및 재고 감소 처리
//            order.setStatus(OrderStatus.completed);
//            orderRepository.save(order);
//
//            // 재고 감소 (비동기)
//             //재고 감소 처리 (flashSaleProductId가 없는 상품만)
//            order.getOrderProducts().stream()
//                .filter(orderProduct -> orderProduct.getFlashSaleProductId() == null)  // flashSaleProduct가 아닌 상품들만 처리
//                .forEach(orderProduct -> {
//                    ProductStockDecreaseRequestDto request = new ProductStockDecreaseRequestDto(orderProduct.getQuantity());
//                    feignClientService.decreaseProductStock(orderProduct.getProductId(), request);
//                });
//
//        } else if (event.paymentStatus() == PaymentStatus.failed) {
//            // 결제 실패 시, 주문 삭제 처리
//            orderRepository.delete(order);
//        }
//    }
}
