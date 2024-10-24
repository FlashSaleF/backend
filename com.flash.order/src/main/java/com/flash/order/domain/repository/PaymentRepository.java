package com.flash.order.domain.repository;

import com.flash.order.domain.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    void delete(Payment payment);

    Payment save(Payment payment);

    Optional<Payment> findByPaymentUid(String paymentUid);

    Optional<Payment> findById(UUID paymentId);

    Optional<Payment> findByIdAndIsDeletedFalse(UUID paymentId);

    Page<Payment> findAllByIsDeletedFalse(Pageable pageable);
}
