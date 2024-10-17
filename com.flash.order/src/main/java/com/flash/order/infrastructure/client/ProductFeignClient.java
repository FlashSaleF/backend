package com.flash.order.infrastructure.client;

import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.application.dtos.response.ProductStockDecreaseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "vendor")
public interface ProductFeignClient {

    @GetMapping("/api/internal/products/{productId}")
    ProductResponseDto getProduct(@PathVariable UUID productId);

    @PatchMapping("/api/internal/products/{productId}/stock/decrease")
    ProductStockDecreaseResponseDto decreaseProductStock(
            @PathVariable UUID productId,
            @RequestBody ProductStockDecreaseRequestDto request
    );
}
