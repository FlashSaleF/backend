package com.flash.flashsale.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

public record SchedulerRequestDto(
    @NotNull UUID flashSaleId,
    @NotNull UUID flashSaleProductId,
    @With @NotNull @Future LocalDateTime flashSaleTime
) {
}
