package com.flash.flashsale.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;


public enum FlashSaleErrorCode implements ErrorCode {
    IS_ON_SALE(HttpStatus.BAD_REQUEST, "세일 중인 상품은 수정할 수 없습니다."),
    INVALID_PERMISSION_REQUEST(HttpStatus.FORBIDDEN, "유효하지 않은 권한 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 플래시 세일 입니다."),
    DUPLICATE_DATE(HttpStatus.BAD_REQUEST, "같은 날짜에 진행되는 세일이 있습니다."),
    NOT_AVAILABLE_DATE(HttpStatus.BAD_REQUEST, "종료일은 시작일보다 빠를 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    FlashSaleErrorCode(HttpStatus status, String message) {
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
