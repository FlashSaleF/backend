package com.flash.alarm.infrastructure.repository;

import com.flash.alarm.domain.model.ScheduledTaskEntity;
import com.flash.alarm.domain.model.TaskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaScheduledTaskRepository extends JpaRepository<ScheduledTaskEntity, Long> {

    Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatus(UUID flashSaleProductId, TaskStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM ScheduledTaskEntity t WHERE t.flashSaleProductId = :flashSaleProductId AND t.status = :status")
    Optional<ScheduledTaskEntity> findByFlashSaleProductIdAndStatusWithLock(UUID flashSaleProductId, TaskStatus status);
}
