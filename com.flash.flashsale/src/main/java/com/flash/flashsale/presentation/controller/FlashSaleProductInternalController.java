package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.response.InternalProductResponseDto;
import com.flash.flashsale.application.service.FlashSaleProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/flash-sale-products")
public class FlashSaleProductInternalController {

    private final FlashSaleProductService flashSaleProductService;

    @GetMapping("/{productId}")
    public InternalProductResponseDto get(@PathVariable("productId") UUID productId) {
        return flashSaleProductService.getOneByProductId(productId);
    }
}
