package com.flash.vendor.infrastructure.client;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ClientErrorCode implements ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다. 요청 데이터를 확인해 주세요."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "현재 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해 주세요."),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String message;

    ClientErrorCode(HttpStatus status, String message) {
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
