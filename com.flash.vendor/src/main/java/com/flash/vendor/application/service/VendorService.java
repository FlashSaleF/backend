package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.VendorMapper;
import com.flash.vendor.infrastructure.client.UserFeignClient;
import com.flash.vendor.application.dto.request.VendorRequestDto;
import com.flash.vendor.application.dto.response.UserResponseDto;
import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorMapper vendorMapper;
    private final UserFeignClient userFeignClient;
    private final VendorRepository vendorRepository;
    public VendorResponseDto createVendor(VendorRequestDto request) {

        validateAddressUniqueness(request.address());

        //TODO FeignClient Exception Handling
        UserResponseDto userInfo = userFeignClient.getUserInfo(getCurrentUserId());

        Vendor vendor = Vendor.createVendor(
                Long.valueOf(getCurrentUserId()),
                userInfo.name(),
                request.name(),
                request.address()
        );

        Vendor savedVendor = vendorRepository.save(vendor);

        return vendorMapper.convertToResponseDto(savedVendor);

    }

    private void validateAddressUniqueness(String address) {

        Optional<Vendor> optionalVendor = vendorRepository.findByAddress(address);

        if (optionalVendor.isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "이미 등록되어 있는 주소입니다.");
        }
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public VendorResponseDto getVendor(UUID vendorId) {

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 업체가 없습니다."));

        return convertToResponseDto(vendor);
    }
}
