package com.flash.order.infrastructure.repository;

import com.flash.order.domain.model.Order;
import com.flash.order.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<Order, UUID>, OrderRepository {
}
