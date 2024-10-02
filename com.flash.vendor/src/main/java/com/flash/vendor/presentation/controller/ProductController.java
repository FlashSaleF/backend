package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
