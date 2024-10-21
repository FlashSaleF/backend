package com.flash.order.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "flash-sale")
public interface FlashSaleProductFeignClient {

    @PatchMapping("/api/internal/flash-sale-products/{flashSaleProductId}/decreaseStock")
    void decreaseStock(@PathVariable("flashSaleProductId") UUID flashSaleProductId);

    @PatchMapping("/api/internal/flash-sale-products/{flashSaleProductId}/increaseStock")
    void increaseStock(@PathVariable("flashSaleProductId") UUID flashSaleProductId);
}
