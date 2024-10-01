package com.flash.flashsale.application.dto.mapper;

import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FlashSaleProductMapper {

    private final FlashSaleMapper flashSaleMapper;

    public FlashSaleProductResponseDto convertToResponseDto(FlashSaleProduct flashSaleProduct) {
        return new FlashSaleProductResponseDto(
            flashSaleProduct.getId(),
            flashSaleProduct.getProductId(),
            flashSaleProduct.getSalePrice(),
            flashSaleProduct.getStock(),
            flashSaleProduct.getStatus().toString(),
            flashSaleProduct.getStartTime(),
            flashSaleProduct.getEndTime(),
            flashSaleMapper.convertToResponseDto(flashSaleProduct.getFlashSale())
        );
    }
}
