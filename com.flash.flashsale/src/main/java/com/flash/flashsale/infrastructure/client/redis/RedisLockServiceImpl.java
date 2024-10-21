package com.flash.flashsale.infrastructure.client.redis;

import com.flash.base.exception.CustomException;
import com.flash.flashsale.application.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockServiceImpl implements RedisLockService {

    private final RedissonClient redissonClient;

    @Override
    public void lockAndExecute(String lockKey, Runnable task) {

        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        int attempts = 0;
        int maxRetries = 2;

        while (attempts < maxRetries) {
            try {
                isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
                if (isLocked) {
                    task.run();
                    return;
                } else {
                    attempts++;
                }
            } catch (InterruptedException e) {
                throw new CustomException(RedisErrorCode.LOCK_EXECUTION_ERROR);
            } catch (CustomException e) {
                throw e;
            } catch (Exception e) {
                throw new CustomException(RedisErrorCode.UNKNOWN_ERROR);
            } finally {
                if (isLocked) {
                    lock.unlock();
                }
            }
        }

        throw new CustomException(RedisErrorCode.LOCK_ACQUISITION_FAILED);
    }
}
