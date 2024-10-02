package com.flash.order.presentation.controller;

import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.application.service.OrderService;
import com.flash.order.application.dtos.request.OrderRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto response = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(response);
    }

    // 주문 ID로 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID orderId) {
        OrderResponseDto orderResponse = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderResponse);
    }

//    // 사용자 ID로 조회
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@PathVariable Long userId) {
//        List<OrderResponseDto> orders = orderService.getOrdersByUserId(userId);
//        return ResponseEntity.ok(orders);
//    }

    // 모든 주문 조회
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderResponseDto> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderRequestDto orderRequestDto) {

        OrderResponseDto updatedOrder = orderService.updateOrder(orderId, orderRequestDto);

        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
