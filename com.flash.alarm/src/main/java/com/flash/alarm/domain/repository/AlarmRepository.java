package com.flash.alarm.domain.repository;

import com.flash.alarm.domain.model.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlarmRepository {

    List<Alarm> findAllByProductId(UUID productId);

    Alarm save(Alarm alarm);

    Optional<Alarm> findById(UUID id);

    Page<Alarm> findAll(Pageable pageable);

    Page<Alarm> findByUserEmailContaining(String userEmail, Pageable pageable);

    Optional<Alarm> findByFlashSaleProductIdAndUserId(UUID uuid, Long id);
}
