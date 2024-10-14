package com.flash.order.application.service;

import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.application.dtos.response.ProductStockDecreaseResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface FeignClientService {
    ProductResponseDto getProduct(UUID productId);

    void decreaseProductStock(
            UUID productId, ProductStockDecreaseRequestDto request
    );

    void decreaseFlashSaleProductStock(UUID flashSaleProductId);
}
