package com.flash.vendor.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ProductErrorCode implements ErrorCode {

    CANNOT_MODIFY_PRODUCT(HttpStatus.FORBIDDEN, "해당 업체의 상품만 수정할 수 있습니다."),
    INVALID_PERMISSION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 권한 요청입니다."),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "수량은 0보다 커야 합니다.");

    private final HttpStatus status;
    private final String message;

    ProductErrorCode(HttpStatus status, String message) {
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
