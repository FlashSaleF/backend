package com.flash.vendor.domain.exception;

import com.flash.base.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum VendorErrorCode implements ErrorCode {

    ADDRESS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 주소입니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID로 등록된 업체가 없습니다."),
    NOT_COMPANY_OWNER(HttpStatus.FORBIDDEN, "해당 업체 대표자가 아닙니다."),
    INVALID_PERMISSION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 권한 요청입니다."),
    USER_PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 권한이 존재하지 않습니다."),
    VENDOR_DUPLICATION_ERROR(HttpStatus.BAD_REQUEST, "운영중인 하나의 업체만 등록 가능합니다.");

    private final HttpStatus status;
    private final String message;

    VendorErrorCode(HttpStatus status, String message) {
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
