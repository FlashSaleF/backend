package com.flash.alarm.infrastructure.repository;

import com.flash.alarm.domain.model.Alarm;
import com.flash.alarm.domain.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {

    private final JpaAlarmRepository alarmRepository;

    @Override
    public List<Alarm> findAllByProductId(UUID productId) {
        return alarmRepository.findAllByFlashSaleProductId(productId);
    }

    @Override
    public Alarm save(Alarm alarm) {
        return alarmRepository.save(alarm);
    }

    @Override
    public Optional<Alarm> findById(UUID id) {
        return alarmRepository.findById(id);
    }

    @Override
    public Page<Alarm> findAll(Pageable pageable) {
        return alarmRepository.findAll(pageable);
    }

    @Override
    public Page<Alarm> findByUserEmailContaining(String userEmail, Pageable pageable) {
        return alarmRepository.findByUserEmailContaining(userEmail, pageable);
    }

    @Override
    public Optional<Alarm> findByFlashSaleProductIdAndUserId(UUID uuid, Long id) {
        return alarmRepository.findByFlashSaleProductIdAndUserId(uuid, id);
    }
}
