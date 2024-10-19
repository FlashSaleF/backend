package com.flash.alarm.domain.repository;

import com.flash.alarm.domain.model.ScheduledTaskEntity;
import com.flash.alarm.domain.model.TaskStatus;

import java.util.Optional;
import java.util.UUID;

public interface ScheduledTaskRepository {
    Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatus(UUID uuid, TaskStatus taskStatus);

    ScheduledTaskEntity save(ScheduledTaskEntity scheduledTaskEntity);

    Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatusWithLock(UUID flashSaleProductId, TaskStatus status);
}
