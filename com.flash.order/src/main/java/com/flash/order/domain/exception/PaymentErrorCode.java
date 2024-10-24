package com.flash.order.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "결제가 완료되지 않았습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 위변조가 의심됩니다."),
    PAYMENT_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류 발생"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 조회 중 오류 발생"),
    PAYMENT_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 결제 내역을 찾을 수 없습니다."),
    REFUND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "환불에 실패했습니다."),
    REFUND_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "환불 처리 중 오류 발생");

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message){
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
