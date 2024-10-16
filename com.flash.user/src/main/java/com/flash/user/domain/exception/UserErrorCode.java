package com.flash.user.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    DUPLICATED_PHONE(HttpStatus.CONFLICT, "휴대폰 번호가 중복됩니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "E-Mail이 중복됩니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "올바른 권한 코드를 입력하세요"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorCode(HttpStatus status, String message) {
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
