package com.flash.alarm.infrastructure.repository;

import com.flash.alarm.domain.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaAlarmRepository extends JpaRepository<Alarm, UUID> {

    List<Alarm> findAllByFlashSaleProductId(UUID productId);

    Page<Alarm> findByUserEmailContaining(String userEmail, Pageable pageable);

    Optional<Alarm> findByFlashSaleProductIdAndUserId(UUID uuid, Long id);
}
