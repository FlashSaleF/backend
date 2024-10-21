package com.flash.alarm.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AlarmErrorCode implements ErrorCode {
    MAIL_NOT_SENT(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 플래시 세일 상품입니다."),
    INVALID_TIME(HttpStatus.BAD_REQUEST, "알람 시간이 지나거나 임박하여 설정하지 못합니다"),
    DUPLICATED_ALARM(HttpStatus.CONFLICT, "이미 알람 설정한 상품입니다."),
    ALARM_NOT_FOUND(HttpStatus.NOT_FOUND, "유효한 알람 내역이 없습니다"),

    SCHEDULER_JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "등록된 작업 수행에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;

    AlarmErrorCode(HttpStatus status, String message) {
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
