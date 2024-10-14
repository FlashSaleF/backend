package com.flash.order.infrastructure.client;

import com.flash.order.application.dtos.request.ProductStockDecreaseRequestDto;
import com.flash.order.application.dtos.response.ProductResponseDto;
import com.flash.order.application.dtos.response.ProductStockDecreaseResponseDto;
import com.flash.order.application.service.FeignClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeignClientServiceImpl implements FeignClientService {

    private final ProductFeignClient productFeignClient;
    private final FlashSaleProductFeignClient flashSaleProductFeignClient;

    @Override
    public ProductResponseDto getProduct(UUID productId){
        return productFeignClient.getProduct(productId);
    }

    @Override
    public void decreaseProductStock(
            UUID productId,
            ProductStockDecreaseRequestDto request
    ){
        productFeignClient.decreaseProductStock(productId, request);
    }

    @Override
    public void decreaseFlashSaleProductStock(UUID flashSaleProductId){
        flashSaleProductFeignClient.decreaseStock(flashSaleProductId);
    }

}
