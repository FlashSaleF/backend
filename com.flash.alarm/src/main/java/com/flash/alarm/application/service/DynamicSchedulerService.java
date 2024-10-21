package com.flash.alarm.application.service;

import com.flash.alarm.application.dto.reqeuest.SchedulerRequestDto;
import com.flash.alarm.application.service.util.ScheduledTaskMapper;
import com.flash.alarm.domain.exception.AlarmErrorCode;
import com.flash.alarm.domain.model.ScheduledTaskEntity;
import com.flash.alarm.domain.model.TaskStatus;
import com.flash.alarm.domain.repository.ScheduledTaskRepository;
import com.flash.base.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 스케줄러의 시간을 동적으로 받기 위한 클래스
 * 판매자가 Flash Sale Product를 등록하고
 * 관리자가 승인을 하면!
 * 세일 시간을 가지고 FeignClient 요청을 보낼 때 사용.
 */
@Slf4j(topic = "Dynamic Scheduler Service")
@Service
@RequiredArgsConstructor
public class DynamicSchedulerService {
    private final TaskScheduler taskScheduler;
    private final AlarmService alarmService;
    private final ScheduledTaskRepository scheduledTaskRepository;

    // Long은 taskId. 이를 사용하여 여러 개의 스케줄을 개별적으로 관리 가능.
    // ScheduledFuture: 스케줄링된 작업의 실행 상태를 추적하고 제어하는 객체.
    private Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Flash Sale Product가 등록될 때, FeignClient 요청으로 메서드 실행
     */
    // 특정 메서드에서 동적으로 여러 스케줄링 작업 실행.
    @Transactional
    public Long scheduleTaskByFlashSaleTime(SchedulerRequestDto schedulerRequestDto) {
        // 기존 예약된 작업이 있으면 취소
        cancelExistingTaskIfPresent(schedulerRequestDto);

        // Flash Sale이 열리는 1시간 전에 알림 발송
        // Todo: 시간 변경 - minus 1 hour
        SchedulerRequestDto schedulerDto = schedulerRequestDto.withFlashSaleTime(schedulerRequestDto.flashSaleTime().minusMinutes(5));

        // 새 작업을 스케줄링하고 DB 및 Map에 저장
        return scheduleNewTask(schedulerDto);
    }

    @Transactional
    public void cancelExistingTaskIfPresent(SchedulerRequestDto schedulerRequestDto) {
        Optional<ScheduledTaskEntity> task = scheduledTaskRepository.findByFlashSaleProductIdAndStatusWithLock(schedulerRequestDto.flashSaleProductId(), TaskStatus.SCHEDULED);
        if (!task.isPresent()) {
            log.info("해당 상품으로 등록된 기존 스케줄러가 없음");
            return;
        }

        if (task.isPresent() && scheduledTasks.containsKey(task.get().getId())) {
            // 기존 작업이 있을 경우 취소
            scheduledTasks.get(task.get().getId()).cancel(false); // 실행 중인 상태일 때 강제 중단되지 않고 그 이후 예약에서 제거.

            // 상태를 CANCELLED로 업데이트
            ScheduledTaskEntity scheduledTaskEntity = task.get();
            scheduledTaskEntity.setStatus(TaskStatus.CANCELLED);
            scheduledTaskRepository.save(scheduledTaskEntity);
            scheduledTasks.remove(task.get().getId());
            log.info("already existing scheduled task");
        }
    }


    @Transactional
    public Long scheduleNewTask(SchedulerRequestDto schedulerRequestDto) {
        // 새 작업 스케줄링
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(() -> {
            try {
                if (alarmService.sendFlashSaleNotification(schedulerRequestDto.flashSaleProductId())) {
                    // 작업 완료 후 COMPLETED
                    markTaskStatus(schedulerRequestDto.flashSaleProductId(), TaskStatus.COMPLETED);
                } else {
                    // 보낼 대상이 없을 때 CANCELLED
                    markTaskStatus(schedulerRequestDto.flashSaleProductId(), TaskStatus.CANCELLED);
                }

            } catch (Exception e) {
                log.error("Error occurred during flash sale notification: {}", e.getMessage());
                markTaskStatus(schedulerRequestDto.flashSaleProductId(), TaskStatus.FAILED);
                throw new CustomException(AlarmErrorCode.SCHEDULER_JOB_FAILED);
            }
        }, Instant.from(schedulerRequestDto.flashSaleTime().atZone(ZoneId.of("Asia/Seoul"))));
        log.info("FlashSaleProduct Id: {}, Scheduled At: {}", schedulerRequestDto.flashSaleProductId(), schedulerRequestDto.flashSaleTime());

        // DB에 작업 정보 저장
        ScheduledTaskEntity savedTask = scheduledTaskRepository.save(ScheduledTaskMapper.entityFrom(schedulerRequestDto));

        // 현재 작동하고 있는 스케줄러에 추가
        scheduledTasks.put(savedTask.getId(), scheduledFuture);

        return savedTask.getId();
    }

    @Transactional
    public void markTaskStatus(UUID flashSaleProductId, TaskStatus status) {
        Optional<ScheduledTaskEntity> task = scheduledTaskRepository.findByFlashSaleProductIdAndStatus(flashSaleProductId, TaskStatus.SCHEDULED);

        if (task.isPresent()) {
            ScheduledTaskEntity scheduledTask = task.get();
            scheduledTask.setStatus(status);
            scheduledTaskRepository.save(scheduledTask);
            scheduledTasks.remove(scheduledTask.getId()); // 작업 완료 후 Map에서 제거
            log.info("FlashSaleProduct Id: {} has been marked as COMPLETED", flashSaleProductId);
        }
    }


//    /**
//     * 특정 스케줄러 취소하는 메서드.
//     * 스케줄러는 상품마다 진행되므로, 플래시 세일 상품 등록이 취소됐을 때 사용.
//     *
//     * @param flashSaleProductId
//     * @param flashSaleTime
//     */
//    public void cancelTask(UUID flashSaleProductId, Date flashSaleTime) {
//        // TODO: 상태가 SCHEDULED인 애들만 가져와야 함.
//        ScheduledTaskEntity task = scheduledTaskRepository.findByFlashSaleProductIdAndFlashSaleTime().orElseThrow(
//                // TODO: 커스텀 예외로 변경
//                () -> throw new RuntimeException("커스텀 예외로 변경");
//                );
//
//        if (scheduledTasks.containsKey(task.getId())) {
//            scheduledTasks.get(task.getId()).cancel(false);
//            scheduledTasks.remove(task.getId());
//            // TODO: DB에서 해당 Task 조회 후 상태 변경
//
//            log.info("Task Id: {} Cancelled", task.getId());
//
//        } else {
//            // TODO: 커스텀 예외
//            throw new RuntimeException("Task ID: " + taskId + " is not scheduled");
//        }
//    }
//
//    public void cancelTaskList(UUID flashSaleId) {
//        List<ScheduledTaskEntity> taskList = scheduledTaskRepository.findByFlashSaleId().orElseThrow(
//                // TODO: 커스텀 예외
//                () -> throw new RuntimeException("커스템 예외로 변경");
//        );
//        if (scheduledTasks.containsKey(taskId)) {
//            scheduledTasks.get(taskId).cancel(false);
//            scheduledTasks.remove(taskId);
//            // TODO: DB에서 해당 Task 조회 후 상태 변경
//
//            log.info("Task Id: {} Cancelled", taskId);
//        } else {
//            // TODO: 커스텀 예외
//            throw new RuntimeException("Task ID: " + taskId + " is not scheduled");
//        }
//    }
//
//
//    /**
//     * 현재 예약된 모든 스케줄러 취소.
//     */
//    // 모든 작업 취소
//    public void cancelAllTasks() {
//        for (Map.Entry<Long, ScheduledFuture<?>> entry : scheduledTasks.entrySet()) {
//            entry.getValue().cancel(false);
//        }
//        scheduledTasks.clear();
//        System.out.println("All tasks cancelled");
//
//        // TODO: DB에서 모든 작업 상태 업데이트. entry.getKey()를 통해 taskId 가져와서 진행
//
//    }

}
