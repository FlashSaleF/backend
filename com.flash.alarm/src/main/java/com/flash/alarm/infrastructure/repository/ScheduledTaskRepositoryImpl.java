package com.flash.alarm.infrastructure.repository;

import com.flash.alarm.domain.model.ScheduledTaskEntity;
import com.flash.alarm.domain.model.TaskStatus;
import com.flash.alarm.domain.repository.ScheduledTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ScheduledTaskRepositoryImpl implements ScheduledTaskRepository {

    private final JpaScheduledTaskRepository jpaScheduledTaskRepository;

    @Override
    public Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatus(UUID uuid, TaskStatus taskStatus) {
        return jpaScheduledTaskRepository.findByFlashSaleProductIdAndStatus(uuid, taskStatus);
    }

    @Override
    public ScheduledTaskEntity save(ScheduledTaskEntity scheduledTaskEntity) {
        return jpaScheduledTaskRepository.save(scheduledTaskEntity);
    }

    @Override
    public Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatusWithLock(UUID flashSaleProductId, TaskStatus status) {
        return jpaScheduledTaskRepository.findByFlashSaleProductIdAndStatusWithLock(flashSaleProductId, status);
    }
}
