package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.response.FlashSaleProductResponseDto;
import com.flash.vendor.application.dto.response.UserResponseDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FeignClientService {
    UserResponseDto getUserInfo(String userId);

    FlashSaleProductResponseDto getFlashSaleProductInfo(UUID productId);

    Map<UUID, FlashSaleProductResponseDto> getFlashSaleProductListMap(List<UUID> productIds);
}
