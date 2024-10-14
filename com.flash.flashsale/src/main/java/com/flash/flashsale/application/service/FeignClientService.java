package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.request.ProductStockRequestDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FeignClientService {
    ProductResponseDto getProductInfo(UUID productId);
    Map<UUID, ProductResponseDto> getProductInfoListMap(List<UUID> productIds);
    void decreaseProductStock(UUID productId, Integer stock);

    void startSale(List<UUID> productId);

    void endSale(List<UUID> productIdList);

    void increaseProductStock(List<ProductStockRequestDto> productStocks);
}
