package com.flash.order.domain.repository;

import com.flash.order.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {
    void delete(Payment payment);

    Payment save(Payment payment);

    Optional<Payment> findByPaymentUid(String paymentUid);
}
