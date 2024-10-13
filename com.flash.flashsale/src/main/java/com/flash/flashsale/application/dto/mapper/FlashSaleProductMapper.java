package com.flash.flashsale.application.dto.mapper;

import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.dto.response.InternalProductResponseDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import org.springframework.stereotype.Component;

@Component
public class FlashSaleProductMapper {
    public FlashSaleProductResponseDto convertToResponseDto(FlashSaleProduct flashSaleProduct, FlashSaleResponseDto flashSaleResponseDto, ProductResponseDto productResponseDto) {
        return new FlashSaleProductResponseDto(
                flashSaleProduct.getId(),
                flashSaleProduct.getProductId(),
                flashSaleProduct.getSalePrice(),
                flashSaleProduct.getStock(),
                flashSaleProduct.getStatus(),
                flashSaleProduct.getStartTime(),
                flashSaleProduct.getEndTime(),
            flashSaleResponseDto,
            productResponseDto
        );
    }

    public InternalProductResponseDto convertToInternalProductResponseDto(FlashSaleProduct flashSaleProduct, FlashSaleResponseDto flashSaleResponseDto) {
        return new InternalProductResponseDto(
                flashSaleProduct.getId(),
                flashSaleProduct.getProductId(),
                flashSaleProduct.getSalePrice(),
                flashSaleProduct.getStock(),
                flashSaleProduct.getStatus(),
                flashSaleProduct.getStartTime(),
                flashSaleProduct.getEndTime(),
                flashSaleResponseDto
        );
    }
}
