package com.flash.vendor.presentation.controller;

import com.flash.vendor.application.dto.request.VendorRequestDto;
import com.flash.vendor.application.dto.response.VendorDeleteResponseDto;
import com.flash.vendor.application.dto.response.VendorPageResponseDto;
import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.application.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<VendorPageResponseDto> getVendors(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(vendorService.getVendors(pageable));
    }

    @PutMapping("/{vendorId}")
    public ResponseEntity<VendorResponseDto> updateVendor(
            @PathVariable UUID vendorId,
            @Valid @RequestBody VendorRequestDto request) {

        return ResponseEntity.ok(vendorService.updateVendor(vendorId, request));
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<VendorDeleteResponseDto> deleteVendor(
            @PathVariable UUID vendorId
    ) {

        return ResponseEntity.ok(vendorService.deleteVendor(vendorId));
    }
}
