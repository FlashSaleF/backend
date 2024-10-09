package com.flash.order.infrastructure.client;

import com.flash.order.application.dtos.response.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "vendor")
public interface ProductFeignClient {

    @GetMapping("/api/internal/products/{productId}")
    ProductResponseDto getProduct(@PathVariable UUID productId);
}
