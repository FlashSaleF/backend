package com.flash.order.infrastructure.repository;

import com.flash.order.domain.model.Order;
import com.flash.order.domain.repository.OrderRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {

}
