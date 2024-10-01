package com.flash.order.presentation.controller;

import com.flash.order.application.dtos.OrderResponseDto;
import com.flash.order.application.service.OrderService;
import com.flash.order.presentation.dtos.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto orderRequestDto
    ) {
        OrderResponseDto response = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(response);
    }
}
