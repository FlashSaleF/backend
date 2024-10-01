package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.service.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flash-sales")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;

    @PostMapping()
    public ResponseEntity<FlashSaleResponseDto> create(@Valid @RequestBody FlashSaleRequestDto flashSaleRequestDto) {
        return ResponseEntity.ok(flashSaleService.create(flashSaleRequestDto));
    }
}