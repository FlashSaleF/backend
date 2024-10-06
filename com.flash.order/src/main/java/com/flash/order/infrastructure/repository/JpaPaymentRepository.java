package com.flash.order.infrastructure.repository;

import com.flash.order.domain.model.Payment;
import com.flash.order.domain.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID>, PaymentRepository {
}
