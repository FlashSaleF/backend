package com.flash.order.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 주문입니다."),
    ORDER_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 완료된 주문입니다."),
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "주문할 상품을 찾을 수 없습니다."),
    ORDER_PRODUCT_QUANTITY_EXCEEDED(HttpStatus.BAD_REQUEST, "주문 상품 수량이 초과되었습니다."),
    INVALID_PERMISSION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 권한 요청입니다.");
    private final HttpStatus status;
    private final String message;

    OrderErrorCode(HttpStatus status, String message){
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