package com.flash.order.application.service;

import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.application.dtos.mapper.OrderMapper;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.domain.model.Order;
import com.flash.order.domain.model.OrderProduct;
import com.flash.order.domain.model.Payment;
import com.flash.order.domain.model.PaymentStatus;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.application.dtos.request.OrderRequestDto;
import com.flash.order.domain.repository.PaymentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final FeignClientService feignClientService;
    private final OrderMapper orderMapper;

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
                        throw new IllegalArgumentException("해당 상품을 찾을 수 없습니다.");    // CustomException과 ErrorCode 활용
                    }

                    // OrderProduct 객체 생성
                    return OrderProduct.builder()
                            .productId(orderProductDto.productId()) // Product 객체를 엔티티로 변환
                            .quantity(orderProductDto.quantity())
                            .price(productResponseDto.price())
                            .build();
                })
                .collect(Collectors.toList());

        // TotalPrice 계산
        Double totalPrice = orderProducts.stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();

        //임시로 결제 생성(결제 로직에서 결제가 제대로 진행되지 않으면 db서 삭제됨)
        Payment payment = Payment.builder()
                .userId(orderRequestDto.userId())
                .price(totalPrice.intValue())
                .status(PaymentStatus.pending)
                .build();

        paymentRepository.save(payment);

        // 주문 생성
        Order order = Order.createOrder(
                orderRequestDto,
                totalPrice.intValue(),
                UUID.randomUUID().toString() // 주문 고유 UID 생성
        );

        // 주문에 orderProducts 설정
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(order)); // Order 객체 설정

        // 주문에 orderProducts 설정
        order.setOrderProducts(orderProducts);

        //order에 payment 매핑
        order.setPayment(payment);

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        return orderMapper.convertToResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. 주문 ID: " + orderId));
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
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. 주문 ID: " + orderId));

        // 새로운 주문 상품 목록 생성
        List<OrderProduct> updatedOrderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {
                    OrderProduct orderProduct = new OrderProduct();
                    orderProduct.setProductId(orderProductDto.productId());
                    orderProduct.setQuantity(orderProductDto.quantity());
                    orderProduct.setPrice(orderProductDto.price());
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
                .orElseThrow(() -> new IllegalArgumentException("해당 주문을 찾을 수 없습니다. 주문 ID: " + orderId));

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
                        new ResponseStatusException(BAD_REQUEST, "권한이 존재하지 않습니다."))
                .getAuthority();
    }
}
