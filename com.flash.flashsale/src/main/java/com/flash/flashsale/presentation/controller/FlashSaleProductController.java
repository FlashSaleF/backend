package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.service.FlashSaleProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flash-sale-products")
public class FlashSaleProductController {

    private final FlashSaleProductService flashSaleProductService;

    @PostMapping()
    public ResponseEntity<FlashSaleProductResponseDto> create(@Valid @RequestBody FlashSaleProductRequestDto flashSaleProductRequestDto) {
        return ResponseEntity.ok(flashSaleProductService.create(flashSaleProductRequestDto));
    }
}
