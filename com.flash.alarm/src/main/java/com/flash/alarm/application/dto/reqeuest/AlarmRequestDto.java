package com.flash.alarm.application.dto.reqeuest;

import java.util.UUID;

public record AlarmRequestDto(
        UUID flashSaleProductId,
        String flashSaleProductName,
        UUID flashSaleId
) {
}
