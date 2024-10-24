package com.flash.order.domain.repository;

import com.flash.order.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findByIdAndIsDeletedFalse(UUID orderId);

    List<Order> findByUserIdAndIsDeletedFalse(Long userId);

//    List<Order> findAllByIsDeletedFalse();

    Page<Order> findAllByIsDeletedFalse(Pageable pageable);

    void delete(Order order);

//    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.payment WHERE o.id = :orderUid")
//    Optional<Order> findOrderAndPayment(String orderUid);

    Optional<Order> findOrderByOrderUid(String orderUid);
}
