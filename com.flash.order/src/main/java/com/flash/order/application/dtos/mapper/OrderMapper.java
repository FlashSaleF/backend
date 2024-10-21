package com.flash.order.application.dtos.mapper;

import com.flash.order.application.dtos.request.OrderProductDto;
import com.flash.order.application.dtos.response.OrderResponseDto;
import com.flash.order.domain.model.Order;
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
                                orderProduct.getFlashSaleProductId(),
                                orderProduct.getQuantity()
                        ))
                        .collect(Collectors.toList()),
                order.getAddress(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getPayment().getId(),
                order.getOrderUid()
        );
    }
}
