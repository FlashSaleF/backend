package com.flash.flashsale.infrastructure.client;

import com.flash.flashsale.application.dto.request.ProductListRequestDto;
import com.flash.flashsale.application.dto.response.ProductListResponseDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "vendor", configuration = FeignConfig.class)
public interface VendorFeignClient {

    @GetMapping("/api/internal/products/{productId}")
    ProductResponseDto getProductInfo(@PathVariable UUID productId);

    @PostMapping("/api/internal/products/batch")
    ProductListResponseDto getProductInfoList(@RequestBody ProductListRequestDto productIds);
}
