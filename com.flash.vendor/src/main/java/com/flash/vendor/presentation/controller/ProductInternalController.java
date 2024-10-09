package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.ProductListRequestDto;
import com.flash.vendor.application.dto.request.ProductStockDecreaseRequestDto;
import com.flash.vendor.application.dto.response.ProductListResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.application.dto.response.ProductStockDecreaseResponseDto;
import com.flash.vendor.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal/products")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PostMapping("/batch")
    public ResponseEntity<ProductListResponseDto> getProductsByIds(
            @RequestBody ProductListRequestDto request
    ) {
        return ResponseEntity.ok(productService.getProductsByIds(request.productIds()));
    }

    @PatchMapping("/{productId}/stock/decrease")
    public ResponseEntity<ProductStockDecreaseResponseDto> decreaseProductStock(
            @PathVariable UUID productId,
            @RequestBody ProductStockDecreaseRequestDto request
    ) {
        return ResponseEntity.ok(productService.decreaseProductStock(productId, request));
    }

}
