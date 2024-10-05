package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.VendorMapper;
import com.flash.vendor.application.dto.request.VendorRequestDto;
import com.flash.vendor.application.dto.response.UserResponseDto;
import com.flash.vendor.application.dto.response.VendorDeleteResponseDto;
import com.flash.vendor.application.dto.response.VendorPageResponseDto;
import com.flash.vendor.application.dto.response.VendorResponseDto;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final FeignClientService feignClientService;
    private final VendorRepository vendorRepository;

    @Transactional
    public VendorResponseDto createVendor(VendorRequestDto request) {

        validateAddressUniqueness(request.address());

        UserResponseDto userInfo = feignClientService.getUserInfo(getCurrentUserId());

        Vendor vendor = Vendor.createVendor(
                Long.valueOf(getCurrentUserId()),
                userInfo.name(),
                request.name(),
                request.address()
        );

        Vendor savedVendor = vendorRepository.save(vendor);

        return VendorMapper.convertToResponseDto(savedVendor);

    }

    @Transactional(readOnly = true)
    public VendorResponseDto getVendor(UUID vendorId) {

        Vendor vendor = getVendorById(vendorId);

        return VendorMapper.convertToResponseDto(vendor);
    }

    @Transactional(readOnly = true)
    public VendorPageResponseDto getVendors(Pageable pageable) {

        Page<Vendor> vendors = vendorRepository.findAll(pageable);

        return new VendorPageResponseDto(vendors.map(VendorMapper::convertToResponseDto));
    }

    @Transactional
    public VendorResponseDto updateVendor(UUID vendorId, VendorRequestDto request) {

        Vendor vendor = getVendorBasedOnAuthority(vendorId, getCurrentUserAuthority());

        Vendor updateVendor = vendor.updateVendor(request.name(), request.address());

        return VendorMapper.convertToResponseDto(updateVendor);
    }

    @Transactional
    public VendorDeleteResponseDto deleteVendor(UUID vendorId) {

        Vendor vendor = getVendorBasedOnAuthority(vendorId, getCurrentUserAuthority());

        vendor.delete();

        return new VendorDeleteResponseDto("업체 삭제 성공");
    }

    Vendor getVendorBasedOnAuthority(UUID vendorId, String authority) {
        return switch (authority) {
            case "ROLE_VENDOR" ->
                    getVendorByIdAndUserId(vendorId, Long.valueOf(getCurrentUserId()));
            case "ROLE_MANAGER", "ROLE_MASTER" -> getVendorById(vendorId);
            default ->
                    throw new ResponseStatusException(BAD_REQUEST, "유효하지 않은 권한 요청입니다.");
        };
    }

    private Vendor getVendorById(UUID vendorId) {
        return vendorRepository.findByIdAndIsDeletedFalse(vendorId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 업체가 없습니다."));
    }

    private Vendor getVendorByIdAndUserId(UUID vendorId, Long userId) {
        return vendorRepository.findByIdAndUserIdAndIsDeletedFalse(
                vendorId, userId).orElseThrow(() ->
                new ResponseStatusException(BAD_REQUEST, "해당 업체 대표자가 아닙니다."));
    }

    private void validateAddressUniqueness(String address) {

        Optional<Vendor> optionalVendor = vendorRepository.findByAddressAndIsDeletedFalse(address);

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
