package com.flash.vendor.application.dto.mapper;

import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.domain.model.Vendor;
import org.springframework.stereotype.Component;

@Component
public class VendorMapper {
    public VendorResponseDto convertToResponseDto(Vendor vendor) {
        return new VendorResponseDto(
                vendor.getId(),
                vendor.getUserId(),
                vendor.getUsername(),
                vendor.getName(),
                vendor.getAddress()
        );
    }
}
