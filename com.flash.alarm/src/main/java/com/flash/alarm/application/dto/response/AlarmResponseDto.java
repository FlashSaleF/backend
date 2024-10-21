package com.flash.alarm.application.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AlarmResponseDto(
        UUID id,
        String title,
        String contents,
        Long userId,
        String userEmail,
        UUID flashSaleProductId,
        String flashSaleProductName,
        UUID flashSaleId
) {
}
