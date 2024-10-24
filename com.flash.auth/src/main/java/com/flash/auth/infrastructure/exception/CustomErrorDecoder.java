package com.flash.auth.infrastructure.exception;

import com.flash.base.exception.CustomException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new CustomException(ClientErrorCode.BAD_REQUEST);
            case 403 -> new CustomException(ClientErrorCode.FORBIDDEN);
            case 404 -> new CustomException(ClientErrorCode.RESOURCE_NOT_FOUND);
            case 409 -> new CustomException(ClientErrorCode.DUPLICATED_REQUEST);
            case 500 ->
                    new CustomException(ClientErrorCode.INTERNAL_SERVER_ERROR);
            case 503 ->
                    new CustomException(ClientErrorCode.SERVICE_UNAVAILABLE);

            default -> new CustomException(ClientErrorCode.UNEXPECTED_ERROR);
        };
    }
}
