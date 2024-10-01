package com.flash.order.application.service;

import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.application.dtos.mapper.OrderMapper;
import com.flash.order.domain.model.Order;
import com.flash.order.domain.model.OrderProduct;
import com.flash.order.domain.repository.OrderRepository;
import com.flash.order.application.dtos.request.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        // OrderProduct 객체 생성 및 Product 존재 여부 확인
        List<OrderProduct> orderProducts = orderRequestDto.orderProducts().stream()
                .map(orderProductDto -> {

                    //TODO Product 존재 여부 확인 (FeignClient 사용)

                    // OrderProduct 객체 생성
                    return OrderProduct.builder()
                            .productId(orderProductDto.productId()) // Product 객체를 엔티티로 변환
                            .quantity(orderProductDto.quantity())
                            .price(orderProductDto.price())
                            .build();
                })
                .collect(Collectors.toList());

        // TotalPrice 계산
        Double totalPrice = orderProducts.stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getQuantity())
                .sum();

        // 주문 생성
        Order order = Order.createOrder(
                orderRequestDto,
                totalPrice.intValue(),
                UUID.randomUUID() // 결제 ID 생성
        );

        // 주문에 orderProducts 설정
        orderProducts.forEach(orderProduct -> orderProduct.setOrder(order)); // Order 객체 설정

        // 주문에 orderProducts 설정
        order.setOrderProducts(orderProducts);  // orderProducts를 설정해줍니다

        //TODO: 결제 로직

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        return orderMapper.convertToResponseDto(savedOrder);
    }
}
