package com.flash.order.application.service;

import java.util.concurrent.Callable;

public interface RedisLockService {
    <T> T lockAndExecute(String lockKey, Callable<T> task);
}
