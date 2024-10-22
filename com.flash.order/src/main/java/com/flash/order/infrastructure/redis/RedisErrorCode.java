package com.flash.order.infrastructure.redis;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RedisErrorCode implements ErrorCode {
    LOCK_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "락 시도 중 예기치 않은 오류가 발생했습니다"),
    LOCK_ACQUISITION_FAILED(HttpStatus.TOO_MANY_REQUESTS, "락 획득에 실패하였습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;

    RedisErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
