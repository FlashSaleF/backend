package com.flash.flashsale.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FlashSaleProductErrorCode implements ErrorCode {
    INVALID_PERMISSION_REQUEST(HttpStatus.FORBIDDEN, "유효하지 않은 권한 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 플래시 세일 상품 입니다."),
    DUPLICATE(HttpStatus.BAD_REQUEST, "동일한 플래시 세일 상품이 존재합니다."),
    DUPLICATE_DATE(HttpStatus.BAD_REQUEST, "같은 날짜에 진행되는 세일이 있습니다."),
    NOT_AVAILABLE_DATE(HttpStatus.BAD_REQUEST, "종료일은 시작일보다 빠를 수 없습니다."),
    NOT_AVAILABLE_UPDATE(HttpStatus.BAD_REQUEST, "승인 중이거나 승인 대기중인 플래시 세일 상품만 수정 할 수 있습니다."),
    IS_NOT_PENDING(HttpStatus.BAD_REQUEST, "승인 대기중인 플래시 세일 상품만 승인 할 수 있습니다."),
    IS_ON_SALE_DELETE(HttpStatus.BAD_REQUEST, "세일중인 플래시 세일 상품만 종료 할 수 있습니다."),
    IS_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "세일중이지 않은 플래시 세일 상품입니다."),
    IS_NOT_ON_SALE_OR_MY_ITEM(HttpStatus.BAD_REQUEST, "자신이 생성하지 않았거나 세일중이지 않은 플래시 세일 상품입니다."),
    IS_NOT_ON_SALE_TIME(HttpStatus.BAD_REQUEST, "해당 플래시 세일이 진행중이지 않은 시간입니다."),
    IS_NOT_MY_ITEM(HttpStatus.BAD_REQUEST, "자신이 생성한 플래시 세일 상품만 수정, 삭제 할 수 있습니다.");
    private final HttpStatus status;
    private final String message;

    FlashSaleProductErrorCode(HttpStatus status, String message) {
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