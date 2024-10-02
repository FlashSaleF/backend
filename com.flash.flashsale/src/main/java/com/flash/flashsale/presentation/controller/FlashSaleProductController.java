package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.service.FlashSaleProductService;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flash-sale-products")
public class FlashSaleProductController {

    private final FlashSaleProductService flashSaleProductService;

    @PostMapping()
    public ResponseEntity<FlashSaleProductResponseDto> create(@Valid @RequestBody FlashSaleProductRequestDto flashSaleProductRequestDto) {
        return ResponseEntity.ok(flashSaleProductService.create(flashSaleProductRequestDto));
    }

    @GetMapping()
    public ResponseEntity<List<FlashSaleProductResponseDto>> getList(
        @RequestParam(value = "flashSaleId", required = false) UUID flashSaleId,
        @RequestParam(value = "statusList", required = false) List<FlashSaleProductStatus> statusList
    ) {
        return ResponseEntity.ok(flashSaleProductService.getList(flashSaleId, statusList));
    }

    @PatchMapping("/{flashSaleProductId}/approve")
    public ResponseEntity<String> approve(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        return ResponseEntity.ok(flashSaleProductService.approve(flashSaleProductId));
    }
}
