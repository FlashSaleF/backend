package com.flash.vendor.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ProductErrorCode implements ErrorCode {

    CANNOT_MODIFY_PRODUCT(HttpStatus.FORBIDDEN, "해당 업체의 상품만 수정할 수 있습니다."),
    INVALID_PERMISSION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 권한 요청입니다."),
    INSUFFICIENT_STOCK(HttpStatus.INSUFFICIENT_STORAGE, "재고가 부족합니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "수량은 0보다 커야 합니다."),
    UNKNOWN_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "알 수 없는 상품 상태입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID로 등록된 상품이 없습니다.");

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
