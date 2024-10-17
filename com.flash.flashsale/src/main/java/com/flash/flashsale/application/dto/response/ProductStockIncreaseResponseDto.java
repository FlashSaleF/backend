package com.flash.flashsale.application.dto.response;

import java.util.UUID;

public record ProductStockIncreaseResponseDto(
        UUID productId,
        int status // 오타인가
) {
}
