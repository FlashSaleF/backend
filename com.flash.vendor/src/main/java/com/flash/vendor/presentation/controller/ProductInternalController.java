package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.ProductListRequestDto;
import com.flash.vendor.application.dto.request.ProductStatusUpdateDto;
import com.flash.vendor.application.dto.request.ProductStockDecreaseRequestDto;
import com.flash.vendor.application.dto.request.ProductStockIncreaseRequestDto;
import com.flash.vendor.application.dto.response.ProductListResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.application.dto.response.ProductStockDecreaseResponseDto;
import com.flash.vendor.application.dto.response.ProductStockIncreaseResponseDto;
import com.flash.vendor.application.service.ProductApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/products")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductApplicationService productApplicationService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productApplicationService.getProduct(productId));
    }

    @PostMapping("/batch")
    public ResponseEntity<ProductListResponseDto> getProductsByIds(
            @RequestBody ProductListRequestDto request
    ) {
        return ResponseEntity.ok(
                productApplicationService.getProductsByIds(request.productIds()));
    }

    @PatchMapping("/{productId}/stock/decrease")
    public ResponseEntity<ProductStockDecreaseResponseDto> decreaseProductStock(
            @PathVariable UUID productId,
            @RequestBody ProductStockDecreaseRequestDto request
    ) {
        return ResponseEntity.ok(
                productApplicationService.decreaseProductStock(productId, request));
    }

    @PatchMapping("/{productId}/stock/increase")
    public ResponseEntity<ProductStockIncreaseResponseDto> increaseProductStock(
            @PathVariable UUID productId,
            @RequestBody ProductStockIncreaseRequestDto request
    ) {
        return ResponseEntity.ok(
                productApplicationService.increaseProductStock(productId, request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProductStatus(
            @PathVariable UUID productId, @RequestBody ProductStatusUpdateDto request
    ) {

        return ResponseEntity.ok(
                productApplicationService.updateProductStatusForServer(productId, request));
    }
}
