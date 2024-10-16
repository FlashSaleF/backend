package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.request.ProductStatusUpdateDto;
import com.flash.vendor.application.dto.request.ProductUpdateRequestDto;
import com.flash.vendor.application.dto.response.ProductDeleteResponseDto;
import com.flash.vendor.application.dto.response.ProductPageResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.application.service.ProductApplicationService;
import com.flash.vendor.domain.model.ProductStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto request
    ) {

        return ResponseEntity.ok(productApplicationService.createProduct(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId) {

        return ResponseEntity.ok(productApplicationService.getProduct(productId));
    }

    @GetMapping
    public ResponseEntity<ProductPageResponseDto> getProducts(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(productApplicationService.getProducts(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<ProductPageResponseDto> searchProducts(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "lprice", required = false) Integer lprice,
            @RequestParam(name = "hprice", required = false) Integer hprice,
            @RequestParam(name = "status", required = false, defaultValue = "AVAILABLE") ProductStatus status,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(productApplicationService.searchProducts(name, lprice, hprice, status.name(), pageable));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID productId, @RequestBody ProductUpdateRequestDto request
    ) {

        return ResponseEntity.ok(productApplicationService.updateProduct(productId, request));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProductStatus(
            @PathVariable UUID productId, @RequestBody ProductStatusUpdateDto request
    ) {

        return ResponseEntity.ok(productApplicationService.updateProductStatus(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDeleteResponseDto> deleteProduct(
            @PathVariable UUID productId
    ) {

        return ResponseEntity.ok(productApplicationService.deleteProduct(productId));
    }
}
