package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.service.FlashSaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flash-sales")
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @PostMapping()
    public ResponseEntity<FlashSaleResponseDto> create(@Valid @RequestBody FlashSaleRequestDto flashSaleRequestDto) {
        return ResponseEntity.ok(flashSaleService.create(flashSaleRequestDto));
    }

    @PatchMapping("/{flashSaleId}")
    public ResponseEntity<FlashSaleResponseDto> update(
        @PathVariable("flashSaleId") UUID flashSaleId,
        @Valid @RequestBody FlashSaleRequestDto flashSaleRequestDto
    ) {
        return ResponseEntity.ok(flashSaleService.update(flashSaleId, flashSaleRequestDto));
    }
}
