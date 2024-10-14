package com.flash.flashsale.application.dto.request;

import java.util.List;

public record ProductStockListRequestDto(
    List<ProductStockRequestDto> productStocks
) {
}
