package com.flash.flashsale.application.dto.mapper;

import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import org.springframework.stereotype.Component;

@Component
public class FlashSaleProductMapper {
    public FlashSaleProductResponseDto convertToResponseDto(FlashSaleProduct flashSaleProduct, FlashSaleResponseDto flashSaleResponseDto) {
        return new FlashSaleProductResponseDto(
            flashSaleProduct.getId(),
            flashSaleProduct.getProductId(),
            flashSaleProduct.getSalePrice(),
            flashSaleProduct.getStock(),
            flashSaleProduct.getStatus().toString(),
            flashSaleProduct.getStartTime(),
            flashSaleProduct.getEndTime(),
            flashSaleResponseDto
        );
    }
}
