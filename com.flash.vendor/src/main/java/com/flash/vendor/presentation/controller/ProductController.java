package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.response.ProductPageResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.application.service.ProductService;
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

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto request
    ) {

        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable UUID productId) {

        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping
    private ResponseEntity<ProductPageResponseDto> getProducts(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    @GetMapping("/search")
    private ResponseEntity<ProductPageResponseDto> searchProducts(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "lprice", required = false) Integer lprice,
            @RequestParam(name = "hprice", required = false) Integer hprice,
            @RequestParam(name = "status", required = false, defaultValue = "AVAILABLE") String status,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(productService.searchProducts(name, lprice, hprice, status, pageable));
    }
}
