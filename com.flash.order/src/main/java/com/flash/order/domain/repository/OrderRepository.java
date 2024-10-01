package com.flash.order.domain.repository;

import com.flash.order.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);
}
