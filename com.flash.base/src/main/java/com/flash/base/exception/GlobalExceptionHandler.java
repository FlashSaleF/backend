package com.flash.base.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage())
                .build();

        log.warn("{}", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(BaseErrorCode.INTERNAL_SERVER_ERROR)
                .message(BaseErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .build();

        log.error("Exception is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }

        ErrorResponse response = ErrorResponse.builder()
                .errorCode(BaseErrorCode.INVALID_REQUEST)
                .message(errorMessage.toString())
                .build();

        log.error("MethodArgumentNotValidException is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(BaseErrorCode.INVALID_REQUEST)
                .message(BaseErrorCode.INVALID_REQUEST.getMessage())
                .build();

        log.error("DataIntegrityViolationException is occurred.", e);
        return ResponseEntity.status(response.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(BaseErrorCode.FORBIDDEN)
                .message("접근이 거부되었습니다. 권한이 없습니다.")
                .build();

        log.error("AccessDeniedException is occurred.", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType() == UUID.class) {
                ErrorResponse response = ErrorResponse.builder()
                    .errorCode(BaseErrorCode.INVALID_REQUEST)
                    .message("유효하지 않은 UUID 형식입니다.")
                    .build();

                log.error("Invalid UUID format in request body: ", e);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }
        return handleException(e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleUUIDFormatError(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == UUID.class) { // 필요한 타입이 UUID일 때만 처리
            ErrorResponse response = ErrorResponse.builder()
                .errorCode(BaseErrorCode.INVALID_REQUEST)
                .message("유효하지 않은 UUID 형식입니다.")
                .build();

            log.error("Invalid UUID format: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        return handleException(e); // 다른 타입의 예외는 일반 예외 처리로 전달
    }

}
