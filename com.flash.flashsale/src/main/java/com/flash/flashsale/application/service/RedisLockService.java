package com.flash.flashsale.application.service;

public interface RedisLockService {
    void lockAndExecute(String lockKey, Runnable task);
}
