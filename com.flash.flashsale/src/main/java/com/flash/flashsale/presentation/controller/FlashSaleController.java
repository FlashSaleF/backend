package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
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
    public FlashSaleResponseDto create(@RequestBody FlashSaleRequestDto flashSaleRequestDto) {
        return flashSaleService.create(flashSaleRequestDto);
    }
}