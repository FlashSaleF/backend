package com.flash.order.domain.model;

import com.flash.base.jpa.BaseEntity;
import com.flash.order.application.dtos.request.OrderRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private UUID paymentId;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "payment_id") // 외래키를 설정
    private Payment payment;

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public static Order createOrder(OrderRequestDto orderRequestDto, int totalPrice, UUID paymentId) {
        return Order.builder()
                .address(orderRequestDto.address())
                .totalPrice(totalPrice)
                .status(OrderStatus.pending)
                .paymentId(paymentId)
                .userId(orderRequestDto.userId())
                .build();
    }

    public void updateOrder(OrderRequestDto orderRequestDto, int totalPrice) {
        this.address = orderRequestDto.address();
        this.totalPrice = totalPrice;
    }
}
