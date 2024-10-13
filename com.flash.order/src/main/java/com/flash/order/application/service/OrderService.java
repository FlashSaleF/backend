package com.flash.order.application.service;

import com.flash.base.exception.CustomException;
import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.application.dtos.mapper.OrderMapper;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.domain.exception.OrderErrorCode;
import com.flash.order.domain.model.*;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.application.dtos.request.OrderRequestDto;
import com.flash.order.domain.repository.PaymentRepository;
import com.flash.order.infrastructure.messaging.MessagingProducerService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final FeignClientService feignClientService;
    private final OrderMapper orderMapper;
    private final MessagingProducerService messagingProducerService;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        // OrderProduct 객체 생성 및 Product 존재 여부 확인
        List<OrderProduct> orderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {

                    //TODO Product 존재 여부 확인 (FeignClient 사용)
                    ProductResponseDto productResponseDto;
                    try {
                        productResponseDto = feignClientService.getProduct(orderProductDto.productId());
                    } catch (FeignException.NotFound e) {
                        throw new CustomException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND);
                    }

                    UUID flashSaleProductId = null;
                    if(productResponseDto.flashSaleProductResponseDto() != null) {
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
        messagingProducerService.sendPaymentRequest(savedOrder);

        return orderMapper.convertToResponseDto(savedOrder);
    }

    @Transactional
    public void handlePaymentCompleted(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        order.getPayment().changeStatus(PaymentStatus.completed);

        // 재고 감소 처리 플래그
        boolean stockDecreasedSuccessfully = true;

        // 재고 감소 처리
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            ProductStockDecreaseRequestDto requestDto = new ProductStockDecreaseRequestDto(orderProduct.getQuantity());

            try {
                if (orderProduct.getFlashSaleProductId() == null) { // 일반 상품인 경우
                    messagingProducerService.sendDecreaseProductStock(orderProduct.getProductId(), requestDto); // 이벤트 발행
                } else { // 플래시 세일 상품인 경우
                    messagingProducerService.sendDecreaseFlashProductStock(orderProduct.getFlashSaleProductId(), requestDto); // 이벤트 발행
                }
            } catch (Exception e) {
                // 재고 감소 실패 처리
                stockDecreasedSuccessfully = false;
                log.error("재고 감소 처리 중 오류 발생: {}", e.getMessage());
                break; // 오류 발생 시 루프 종료
            }
        }

        // 재고 감소가 실패한 경우 결제 취소
        if (!stockDecreasedSuccessfully) {
            order.getPayment().changeStatus(PaymentStatus.cancelled); // 결제 상태를 취소로 변경
        }

    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        return orderMapper.convertToResponseDto(order);
    }

//    @Transactional(readOnly = true)
//    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
//        List<Order> orders = orderRepository.findByUserIdAndIsDeletedFalse(userId);
//        return orders.stream()
//                .map(orderMapper::convertToResponseDto)
//                .collect(Collectors.toList());
//    }

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

        // 새로운 주문 상품 목록 생성
        List<OrderProduct> updatedOrderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {

                    ProductResponseDto productResponseDto;
                    try {
                        productResponseDto = feignClientService.getProduct(orderProductDto.productId());
                    } catch (FeignException.NotFound e) {
                        throw new CustomException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND);
                    }

                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProductId(orderProductDto.productId());
                    orderProduct.setQuantity(orderProductDto.quantity());
                    orderProduct.setPrice(productResponseDto.price());
                    orderProduct.setOrder(existingOrder); // 현재 Order에 대한 참조 설정
                    return orderProduct;
                }).collect(Collectors.toList());

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
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

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
