package com.flash.order.application.dtos.mapper;

import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.domain.model.Order;
import com.flash.order.application.dtos.request.OrderProductDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public OrderResponseDto convertToResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getUserId(),
                order.getOrderProducts().stream()
                        .map(orderProduct -> new OrderProductDto(
                                orderProduct.getProductId(),
                                orderProduct.getQuantity()
                        ))
                        .collect(Collectors.toList()),
                order.getAddress(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getOrderUid()
        );
    }
}
