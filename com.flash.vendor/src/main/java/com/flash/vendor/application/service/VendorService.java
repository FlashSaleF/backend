package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.VendorMapper;
import com.flash.vendor.application.dto.request.VendorRequestDto;
import com.flash.vendor.application.dto.response.UserResponseDto;
import com.flash.vendor.application.dto.response.VendorPageResponseDto;
import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.VendorRepository;
import com.flash.vendor.infrastructure.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        return VendorMapper.convertToResponseDto(savedVendor);

    }

    public VendorResponseDto getVendor(UUID vendorId) {

        Vendor vendor = getVendorById(vendorId);

        return VendorMapper.convertToResponseDto(vendor);
    }

    public VendorPageResponseDto getVendors(Pageable pageable) {

        Page<Vendor> vendors = vendorRepository.findAll(pageable);

        return new VendorPageResponseDto(vendors.map(VendorMapper::convertToResponseDto));
    }

    public VendorResponseDto updateVendor(UUID vendorId, VendorRequestDto request) {

        Vendor vendor = getVendorBasedOnAuthority(vendorId, getCurrentUserAuthority());

        Vendor updateVendor = vendor.updateVendor(request.name(), request.address());

        return VendorMapper.convertToResponseDto(updateVendor);
    }

    private Vendor getVendorBasedOnAuthority(UUID vendorId, String authority) {
        return switch (authority) {
            case "VENDOR" ->
                    getVendorByIdAndUserId(vendorId, Long.valueOf(getCurrentUserId()));
            case "MASTER" -> getVendorById(vendorId);
            default ->
                    throw new ResponseStatusException(BAD_REQUEST, "유효하지 않은 권한 요청입니다.");
        };
    }

    private Vendor getVendorById(UUID vendorId) {
        return vendorRepository.findById(vendorId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 업체가 없습니다."));
    }

    private Vendor getVendorByIdAndUserId(UUID vendorId, Long userId) {
        return vendorRepository.findByIdAndUserId(
                vendorId, userId).orElseThrow(() ->
                new ResponseStatusException(BAD_REQUEST, "본인의 업체 정보만 수정할 수 있습니다."));
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

    private String getCurrentUserAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(BAD_REQUEST, "권한이 존재하지 않습니다."))
                .getAuthority();
    }
}
