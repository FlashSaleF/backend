package com.flash.flashsale.application.dto.request;

import java.util.List;
import java.util.UUID;

public record ProductListRequestDto(
        List<UUID> productIds
) {
}