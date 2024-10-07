package com.flash.vendor.infrastructure.client;

import com.flash.vendor.application.dto.response.FlashSaleProductResponseDto;
import com.flash.vendor.application.dto.response.UserResponseDto;
import com.flash.vendor.application.service.FeignClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class FeignClientServiceImpl implements FeignClientService {

    private final UserFeignClient userFeignClient;
    private final FlashSaleFeignClient flashSaleFeignClient;

    @Override
    public UserResponseDto getUserInfo(String userId) {

        return userFeignClient.getUserInfo(userId);
    }

    @Override
    public FlashSaleProductResponseDto getFlashSaleProductInfo(UUID productId) {

        return flashSaleFeignClient.getFlashSaleProductInfo(productId);
    }

    @Override
    public Map<UUID, FlashSaleProductResponseDto> getFlashSaleProductListMap(
            List<UUID> productIds
    ) {
        List<FlashSaleProductResponseDto> saleProducts =
                getFlashSaleProductInfoList(productIds);

        return saleProducts.stream()
                .collect(Collectors.toMap(
                        FlashSaleProductResponseDto::getProductId, Function.identity()));
    }

    public List<FlashSaleProductResponseDto> getFlashSaleProductInfoList(
            List<UUID> productIds
    ) {

        return flashSaleFeignClient.getFlashSaleProductInfoList(productIds);
    }
}
