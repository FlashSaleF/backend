package com.flash.order.infrastructure.redis;

import com.flash.base.exception.CustomException;
import com.flash.order.application.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockServiceImpl implements RedisLockService {

    private final RedissonClient redissonClient;

    @Override
    public <T> T lockAndExecute(String lockKey, Callable<T> task) {

        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        int attempts = 0;
        int maxRetries = 3;

        try {
            while (attempts < maxRetries) {
                try {
                    isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS); // 락 획득 시도 시간, 락 점유 시간, 시간 단위
                    if (isLocked) {
                        return task.call(); // 락을 성공적으로 획득했을 때만 task 실행
                    } else {
                        attempts++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();  // InterruptedException 발생 시 현재 쓰레드의 인터럽트 상태를 유지
                    throw new CustomException(RedisErrorCode.LOCK_EXECUTION_ERROR);
                } catch (CustomException e) {
                    throw e;
                } catch (Exception e) {
                    throw new CustomException(RedisErrorCode.UNKNOWN_ERROR);
                }
            }
        } finally {
            if (isLocked) {
                try {
                    lock.unlock(); // 락이 정상적으로 획득되었을 경우에만 해제
                } catch (IllegalMonitorStateException e) {
                    // 락을 해제할 수 없는 경우에 대한 로그 기록 또는 처리 추가 가능
                    log.error("락 해제 중 오류 발생: {}", e.getMessage());
                }
            }
        }

        // 락 획득에 실패한 경우 CustomException을 던짐
        throw new CustomException(RedisErrorCode.LOCK_ACQUISITION_FAILED);
    }
}
