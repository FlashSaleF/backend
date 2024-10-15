package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.response.InternalProductResponseDto;
import com.flash.flashsale.application.service.FlashSaleProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping()
    public List<InternalProductResponseDto> getList(@RequestBody List<UUID> productIds) {
        return flashSaleProductService.getListByProductIds(productIds);
    }

    @PatchMapping("/{flashSaleProductId}/increaseStock")
    public void increaseStock(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        flashSaleProductService.increaseStock(flashSaleProductId);
    }

    @PutMapping("/{flashSaleProductId}/decreaseStock")
    public void decreaseStock(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        flashSaleProductService.decreaseStock(flashSaleProductId);
    }

    @DeleteMapping("/{productId}/delete")
    public void delete(@PathVariable("productId") UUID productId) {
        flashSaleProductService.deleteByProductId(productId);
    }
}
