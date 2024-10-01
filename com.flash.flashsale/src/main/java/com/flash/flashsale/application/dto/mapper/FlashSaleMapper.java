package com.flash.flashsale.application.dto.mapper;

import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import org.springframework.stereotype.Component;

@Component
public class FlashSaleMapper {
    public FlashSaleResponseDto convertToResponseDto(FlashSale flashSale) {
        return new FlashSaleResponseDto(
            flashSale.getId(),
            flashSale.getName(),
            flashSale.getStartDate(),
            flashSale.getEndDate()
        );
    }
}