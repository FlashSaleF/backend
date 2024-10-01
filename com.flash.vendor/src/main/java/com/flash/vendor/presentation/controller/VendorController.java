package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.VendorRequestDto;
import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.application.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponseDto> createVendor(
            @Valid @RequestBody VendorRequestDto request
    ) {

        return ResponseEntity.ok(vendorService.createVendor(request));
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<VendorResponseDto> getVendor(@PathVariable UUID vendorId) {

        return ResponseEntity.ok(vendorService.getVendor(vendorId));
    }
}
