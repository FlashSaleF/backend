package com.flash.alarm.application.dto.reqeuest;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record SchedulerRequestDto(
        @NotNull UUID flashSaleId,
        @NotNull UUID flashSaleProductId,
        @With @NotNull @Future LocalDateTime flashSaleTime
) {
    /**
     * 메일이 1시간 전에 발송되므로,
     * "현재 시간"이 "flashSaleTime - 2시간"보다 후이면 예외 발생
     *
     * @return
     */
    @AssertTrue(message = "플래시 세일 시간이 임박하거나 이미 시작되어 메일을 보낼 수 없음.")
    public boolean isValidSchedulingTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        // Todo: 시간 변경 minus 2 hours
        return now.isBefore(flashSaleTime.minusMinutes(5));
    }
}
