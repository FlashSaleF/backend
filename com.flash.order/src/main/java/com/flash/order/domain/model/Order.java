package com.flash.order.domain.model;

import com.flash.base.jpa.BaseEntity;
import com.flash.order.application.dtos.request.OrderRequestDto;
import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Long userId;

    private String orderUid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id") // 외래키를 설정
    private Payment payment;

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public void updateOrder(OrderRequestDto orderRequestDto, int totalPrice) {
        this.address = orderRequestDto.address();
        this.totalPrice = totalPrice;
    }

    public void changeOrderStatus(OrderStatus status) {
        this.status = status;
    }

    public void addPayment(Payment payment) {
        this.payment = payment; // Order에 Payment 설정
    }
}
