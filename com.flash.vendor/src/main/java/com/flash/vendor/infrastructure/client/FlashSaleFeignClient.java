package com.flash.vendor.infrastructure.client;

import com.flash.vendor.application.dto.response.FlashSaleProductResponseDto;
import com.flash.vendor.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

// TODO route id or url
@FeignClient(name = "flashsale", configuration = FeignConfig.class)
public interface FlashSaleFeignClient {

    @GetMapping()
    FlashSaleProductResponseDto getFlashSaleProductInfo(@PathVariable UUID productId);

    @PostMapping()
    List<FlashSaleProductResponseDto> getFlashSaleProductInfoList(@RequestBody List<UUID> productIds);

}
