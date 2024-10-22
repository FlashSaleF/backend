package com.flash.order.infrastructure.redis;

import com.flash.base.exception.CustomException;
import com.flash.order.application.service.RedisLockService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockServiceImpl implements RedisLockService {

    private final RedissonClient redissonClient;

    @Override
    public <T> T lockAndExecute(String lockKey, Callable<T> task) {

        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        int attempts = 0;
        int maxRetries = 2;

        while (attempts < maxRetries) {
            try {
                isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
                if (isLocked) {
                    return task.call();
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

        //TODO Lock 획득 실패
        throw new CustomException(RedisErrorCode.LOCK_ACQUISITION_FAILED);
    }
}
