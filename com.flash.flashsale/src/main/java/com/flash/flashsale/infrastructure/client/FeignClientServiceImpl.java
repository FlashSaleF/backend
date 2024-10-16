package com.flash.flashsale.infrastructure.client;

import com.flash.flashsale.application.dto.request.*;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.application.dto.response.ProductStockIncreaseResponseDto;
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

    @Override
    public void decreaseProductStock(UUID productId, Integer stock) {
        vendorFeignClient.decreaseProductStock(productId, new ProductStockDecreaseRequestDto(stock));
    }

    @Override
    public void increaseProductStock(List<ProductStockRequestDto> productStocks) {
        vendorFeignClient.increaseProductStock(new ProductStockListRequestDto(productStocks));
    }

    @Override
    public ProductStockIncreaseResponseDto increaseOneProductStock(UUID productId, Integer stock) {
        return vendorFeignClient.increaseOneProductStock(productId, new ProductStockIncreaseRequestDto(stock));
    }

    @Override
    public ProductResponseDto updateProductStatus(UUID productId, String status) {
        return vendorFeignClient.updateProductStatus(productId, new ProductStatusUpdateDto(status));
    }

    public List<ProductResponseDto> getProductInfoList(List<UUID> productIds) {

        return vendorFeignClient.getProductInfoList(new ProductListRequestDto(productIds)).productList();
    }
}
