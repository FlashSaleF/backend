package com.flash.order.application.service;

import com.flash.base.exception.CustomException;
import com.flash.order.application.dtos.mapper.OrderMapper;
import com.flash.order.application.dtos.request.OrderRequestDto;
import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.request.ProductStockIncreaseRequestDto;
import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.domain.exception.OrderErrorCode;
import com.flash.order.domain.model.*;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.domain.repository.PaymentRepository;
import com.flash.order.infrastructure.messaging.MessagingProducerService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final FeignClientService feignClientService;
    private final OrderMapper orderMapper;
    private final MessagingProducerService messagingProducerService;
    private final PaymentService paymentService;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        // OrderProduct 객체 생성 및 Product 존재 여부 확인
        List<OrderProduct> orderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {

                    ProductResponseDto productResponseDto;
                    try {
                        productResponseDto = feignClientService.getProduct(orderProductDto.productId());
                    } catch (FeignException.NotFound e) {
                        throw new CustomException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND);
                    }

                    // 상품 존재 시 재고 검증 로직 추가
                    if (productResponseDto.stock() < orderProductDto.quantity()) {
                        throw new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK);
                    }

                    UUID flashSaleProductId = null;
                    if (productResponseDto.flashSaleProductResponseDto().isPresent()) {
                        flashSaleProductId = productResponseDto.flashSaleProductResponseDto().get().flashSaleProductId();
                    }

                    // OrderProduct 객체 생성
                    return OrderProduct.builder()
                            .productId(flashSaleProductId == null ? orderProductDto.productId() : null) // Product 객체를 엔티티로 변환
                            .flashSaleProductId(flashSaleProductId)
                            .quantity(orderProductDto.quantity())
                            .price(flashSaleProductId == null ? productResponseDto.price() : productResponseDto.flashSaleProductResponseDto().get().salePrice())
                            .build();
                })
                .collect(Collectors.toList());

        // TotalPrice 계산
        Double totalPrice = orderProducts.stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();

        //임시로 결제 생성(결제 로직에서 결제가 제대로 진행되지 않으면 db서 삭제됨)
        Payment payment = Payment.builder()
                .userId(Long.valueOf(getCurrentUserId()))
                .price(totalPrice.intValue())
                .status(PaymentStatus.pending)
                .build();

        paymentRepository.save(payment);

        // 주문 생성
        Order order = Order.builder()
                .address(orderRequestDto.address())
                .totalPrice(totalPrice.intValue())
                .status(OrderStatus.pending)
                .orderUid(UUID.randomUUID().toString())
                .userId(Long.valueOf(getCurrentUserId()))
                .build();

        // 주문에 orderProducts 설정
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(order)); // Order 객체 설정

        // 주문에 orderProducts 설정
        order.setOrderProducts(orderProducts);

        //order에 payment 매핑
        order.setPayment(payment);

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        //주문 생성 시 이벤트 발행
//        messagingProducerService.sendPaymentRequest(savedOrder);

        return orderMapper.convertToResponseDto(savedOrder);
    }

    //결제 처리 및 재고 감소 이벤트 발행
    @Transactional
    public void handlePaymentCompleted(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

//        order.getPayment().changeStatus(PaymentStatus.completed);

        // 재고 감소 처리 플래그
        boolean stockDecreasedSuccessfully = true;

        // 재고 감소가 성공한 경우에만 계속 처리
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            ProductStockDecreaseRequestDto requestDto = new ProductStockDecreaseRequestDto(orderProduct.getQuantity());

            if (orderProduct.getFlashSaleProductId() == null) {
                messagingProducerService.sendDecreaseProductStock(order.getId(), orderProduct.getProductId(), requestDto);
            } else {
                messagingProducerService.sendDecreaseFlashProductStock(order.getId(), orderProduct.getFlashSaleProductId(), requestDto);
            }
        }
    }

    @Transactional
    public void handleOrderCompleted(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        order.setStatus(OrderStatus.completed);
    }

    @Transactional
    public void handleOrderCancelled(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        order.setStatus(OrderStatus.cancelled);
    }

    @Transactional
    public void cancelOrderAndPayment(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        order.getPayment().changeStatus(PaymentStatus.cancelled); // 결제 상태를 취소로 변경
        order.setStatus(OrderStatus.cancelled); // 주문 상태를 취소로 변경
        orderRepository.save(order); // 상태 변경 후 저장
    }

    //주문 조회
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {

        Long currentUserId = Long.valueOf(getCurrentUserId());
        String authority = getCurrentUserAuthority();

        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // 권한이 ROLE_MASTER가 아닌 경우에만 주문자가 맞는지 확인
        if (!authority.equals("ROLE_MASTER") && !order.getUserId().equals(currentUserId)) {
            throw new CustomException(OrderErrorCode.INVALID_PERMISSION_REQUEST);
        }

        return orderMapper.convertToResponseDto(order);
    }

    //주문 전체 조회
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAllByIsDeletedFalse(pageable);
        return orders.map(orderMapper::convertToResponseDto);
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, OrderRequestDto orderRequestDto) {
        // 기존 주문 조회
        Order existingOrder = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // 기존 주문의 상품 목록 저장
        List<OrderProduct> existingOrderProducts = new ArrayList<>(existingOrder.getOrderProducts());

        // 새로운 주문 상품 목록 생성
        List<OrderProduct> updatedOrderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {

                    ProductResponseDto productResponseDto;
                    try {
                        productResponseDto = feignClientService.getProduct(orderProductDto.productId());
                    } catch (FeignException.NotFound e) {
                        throw new CustomException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND);
                    }

                    // 재고 검증 로직 추가
                    if (productResponseDto.stock() < orderProductDto.quantity()) {
                        throw new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK);
                    }

                    UUID flashSaleProductId = null;
                    if (productResponseDto.flashSaleProductResponseDto().isPresent()) {
                        flashSaleProductId = productResponseDto.flashSaleProductResponseDto().get().flashSaleProductId();
                    }

                    // OrderProduct 객체 생성
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProductId(flashSaleProductId == null ? orderProductDto.productId() : null);
                    orderProduct.setFlashSaleProductId(flashSaleProductId);
                    orderProduct.setQuantity(orderProductDto.quantity());
                    orderProduct.setPrice(flashSaleProductId == null ? productResponseDto.price() : productResponseDto.flashSaleProductResponseDto().get().salePrice());
                    orderProduct.setOrder(existingOrder); // 현재 Order에 대한 참조 설정
                    return orderProduct;
                }).collect(Collectors.toList());


        for (OrderProduct excludedProduct : existingOrderProducts) {
            ProductStockIncreaseRequestDto requestDto = new ProductStockIncreaseRequestDto(excludedProduct.getQuantity());

            // 재고 복구 요청 (flashSaleProduct 여부에 따라 분기 처리)
            if (excludedProduct.getFlashSaleProductId() == null) {
                messagingProducerService.sendIncreaseProductStock(existingOrder.getId(), excludedProduct.getProductId(), requestDto);
            } else {
                messagingProducerService.sendIncreaseFlashProductStock(existingOrder.getId(), excludedProduct.getFlashSaleProductId(), requestDto);
            }
        }

        // 새롭게 추가된 상품에 대해 재고 감소 처리
        for (OrderProduct orderProduct : updatedOrderProducts) {
            ProductStockDecreaseRequestDto requestDto = new ProductStockDecreaseRequestDto(orderProduct.getQuantity());

            // 재고 감소 요청 (flashSaleProduct 여부에 따라 분기 처리)
            if (orderProduct.getFlashSaleProductId() == null) {
                messagingProducerService.sendDecreaseProductStock(orderId, orderProduct.getProductId(), requestDto);
            } else {
                messagingProducerService.sendDecreaseFlashProductStock(orderId, orderProduct.getFlashSaleProductId(), requestDto);
            }
        }

        // 총 금액 다시 계산
        Double totalPrice = updatedOrderProducts.stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();

        // 주문 업데이트
        existingOrder.updateOrder(orderRequestDto, totalPrice.intValue());

        // 기존 상품 목록과 새로운 목록을 관리
        existingOrder.getOrderProducts().clear(); // 기존 리스트를 비우고
        existingOrder.getOrderProducts().addAll(updatedOrderProducts); // 새 상품 추가

        // 주문 다시 저장
        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.convertToResponseDto(updatedOrder);
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        // 기존 주문 조회
        Order existingOrder = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // 이미 취소된 주문인지 확인
        if (existingOrder.getStatus() == OrderStatus.cancelled) {
            throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELLED);
        }

        // 결제 취소 요청 이벤트
//        messagingProducerService.sendCancelPayment(orderId);
        paymentService.refundPayment(existingOrder.getPayment().getPaymentUid());

        // 주문에 포함된 상품들에 대한 재고 복구 요청
        for (OrderProduct orderProduct : existingOrder.getOrderProducts()) {
            ProductStockIncreaseRequestDto requestDto = new ProductStockIncreaseRequestDto(orderProduct.getQuantity());

            // 재고 복구 요청 (flashSaleProduct 여부에 따라 분기 처리)
            if (orderProduct.getFlashSaleProductId() == null) {
//                messagingProducerService.sendIncreaseProductStock(existingOrder.getId(), orderProduct.getProductId(), requestDto);
                feignClientService.increaseProductStock(orderProduct.getProductId(), requestDto);
            } else {
//                messagingProducerService.sendIncreaseFlashProductStock(existingOrder.getId(), orderProduct.getFlashSaleProductId(), requestDto);
                feignClientService.increaseFlashSaleProductStock(orderProduct.getFlashSaleProductId());
            }
        }

    }

    @Transactional
    public void rollbackOrder(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        cancelOrderAndPayment(orderId);
        order.getPayment().delete();
        order.delete();
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentUserAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new CustomException(OrderErrorCode.INVALID_PERMISSION_REQUEST))
                .getAuthority();
    }
}
