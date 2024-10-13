package com.flash.flashsale.infrastructure.client;

import com.flash.flashsale.application.dto.request.ProductListRequestDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.application.service.FeignClientService;
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
    private final VendorFeignClient vendorFeignClient;

    @Override
    public ProductResponseDto getProductInfo(UUID productId) {
        return vendorFeignClient.getProductInfo(productId);
    }

    @Override
    public Map<UUID, ProductResponseDto> getProductInfoListMap(List<UUID> productIds) {

        return getProductInfoList(productIds).stream()
            .collect(Collectors.toMap(ProductResponseDto::id, Function.identity()));
    }

    public List<ProductResponseDto> getProductInfoList(List<UUID> productIds) {

        return vendorFeignClient.getProductInfoList(new ProductListRequestDto(productIds)).productList();
    }
}
