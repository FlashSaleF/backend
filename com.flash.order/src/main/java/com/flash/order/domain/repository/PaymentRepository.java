package com.flash.order.domain.repository;

import com.flash.order.domain.model.Payment;

public interface PaymentRepository {
    void delete(Payment payment);

    void save(Payment payment);
}
