package com.flash.order.domain.model;

import com.flash.base.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private PaymentStatus status;

    private String paymentUid;

    @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
    private Order order;

    @Builder
    public Payment(Long userId, int price){
        this.userId = userId;
        this.price = price;
        this.status = PaymentStatus.pending;
    }

    public void changePaymentBySuccess(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
    }

    public void changePaymentByCancell(PaymentStatus status, String paymentUid) {
        this.status = status;
        this.paymentUid = paymentUid;
        this.delete();
    }

    public void changeStatus(PaymentStatus status) {
        this.status = status;
    }
}
