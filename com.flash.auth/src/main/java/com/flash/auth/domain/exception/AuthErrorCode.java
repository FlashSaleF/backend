package com.flash.auth.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCode {
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다."),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EMPTY_JWT(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    JWT_NOT_FOUND(HttpStatus.FORBIDDEN, "JWT 토큰이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
