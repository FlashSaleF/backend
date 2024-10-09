package com.flash.flashsale.presentation.controller;

import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.service.FlashSaleProductService;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/{flashSaleProductId}")
    public ResponseEntity<FlashSaleProductResponseDto> getOne(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        return ResponseEntity.ok(flashSaleProductService.getOne(flashSaleProductId));
    }

    @GetMapping()
    public ResponseEntity<List<FlashSaleProductResponseDto>> getList(
        @RequestParam(value = "flashSaleId", required = false) UUID flashSaleId,
        @RequestParam(value = "statusList", required = false) List<FlashSaleProductStatus> statusList
    ) {
        return ResponseEntity.ok(flashSaleProductService.getList(flashSaleId, statusList));
    }

    @GetMapping("/time")
    public ResponseEntity<List<FlashSaleProductResponseDto>> getListByTime(
        @DateTimeFormat(pattern = "yyyy-MM-dd HH") @RequestParam(value = "startTime") LocalDateTime startTime,
        @DateTimeFormat(pattern = "yyyy-MM-dd HH") @RequestParam(value = "endTime") LocalDateTime endTime
    ) {
        return ResponseEntity.ok(flashSaleProductService.getListByTime(startTime, endTime));
    }

    @PatchMapping("/{flashSaleProductId}")
    public ResponseEntity<FlashSaleProductResponseDto> update(
        @PathVariable("flashSaleProductId") UUID flashSaleProductId,
        @Valid @RequestBody FlashSaleProductRequestDto flashSaleProductRequestDto
    ) {
        return ResponseEntity.ok(flashSaleProductService.update(flashSaleProductId, flashSaleProductRequestDto));
    }

    @PatchMapping("/{flashSaleProductId}/approve")
    public ResponseEntity<String> approve(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        return ResponseEntity.ok(flashSaleProductService.approve(flashSaleProductId));
    }

    @PatchMapping("/{flashSaleProductId}/refuse")
    public ResponseEntity<String> refuse(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        return ResponseEntity.ok(flashSaleProductService.refuse(flashSaleProductId));
    }

    @PatchMapping("/{flashSaleProductId}/end")
    public ResponseEntity<String> endSale(@PathVariable("flashSaleProductId") UUID flashSaleProductId) {
        return ResponseEntity.ok(flashSaleProductService.endSale(flashSaleProductId));
    }

    @DeleteMapping("/{flashSaleProductId}")
    public ResponseEntity<String> delete(
        @PathVariable("flashSaleProductId") UUID flashSaleProductId
    ) {
        return ResponseEntity.ok(flashSaleProductService.delete(flashSaleProductId));
    }
}
