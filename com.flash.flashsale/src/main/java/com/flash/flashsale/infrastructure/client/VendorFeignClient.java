package com.flash.flashsale.infrastructure.client;

import com.flash.flashsale.application.dto.request.ProductListRequestDto;
import com.flash.flashsale.application.dto.request.ProductStockDecreaseRequestDto;
import com.flash.flashsale.application.dto.request.ProductStockListRequestDto;
import com.flash.flashsale.application.dto.response.ProductListResponseDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.application.dto.response.ProductStockDecreaseResponseDto;
import com.flash.flashsale.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "vendor", configuration = FeignConfig.class)
public interface VendorFeignClient {

    @GetMapping("/api/internal/products/{productId}")
    ProductResponseDto getProductInfo(@PathVariable UUID productId);

    @PostMapping("/api/internal/products/batch")
    ProductListResponseDto getProductInfoList(@RequestBody ProductListRequestDto productIds);

    @PatchMapping("/api/internal/products/{productId}/stock/decrease")
    ProductStockDecreaseResponseDto decreaseProductStock(
        @PathVariable UUID productId,
        @RequestBody ProductStockDecreaseRequestDto request
    );

    @PatchMapping("/api/internal/products/stock/increase")
    void increaseProductStock(
        @RequestBody ProductStockListRequestDto request
    );

    @PatchMapping("/api/internal/products/start-sale")
    void startSale(@RequestBody ProductListRequestDto request);

    @PatchMapping("/api/internal/products/end-sale")
    void endSale(@RequestBody ProductListRequestDto request);
}
