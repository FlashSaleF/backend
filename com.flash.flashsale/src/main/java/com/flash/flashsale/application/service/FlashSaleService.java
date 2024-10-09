package com.flash.flashsale.application.service;

import com.flash.base.exception.CustomException;
import com.flash.flashsale.application.dto.mapper.FlashSaleMapper;
import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.exception.FlashSaleErrorCode;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleMapper flashSaleMapper;
    private final FlashSaleRepository flashSaleRepository;

    @Transactional
    public FlashSaleResponseDto create(FlashSaleRequestDto flashSaleRequestDto) {
        validAdmin();
        validAvailableDate(flashSaleRequestDto);
        validDuplicateDate(flashSaleRequestDto);

        FlashSale flashSale = flashSaleRepository.save(FlashSale.create(flashSaleRequestDto));

        return flashSaleMapper.convertToResponseDto(flashSale);
    }

    @Transactional
    public FlashSaleResponseDto update(UUID flashSaleId, FlashSaleRequestDto flashSaleRequestDto) {
        validAdmin();
        validDuplicateDate(flashSaleRequestDto);
        validAvailableDate(flashSaleRequestDto);

        FlashSale flashSale = existFlashSale(flashSaleId);

        if (isOnSale(flashSale)) {
            throw new CustomException(FlashSaleErrorCode.IS_ON_SALE);
        }

        flashSale.update(flashSaleRequestDto);

        return flashSaleMapper.convertToResponseDto(flashSale);
    }

    @Transactional
    public List<FlashSaleResponseDto> availableList() {
        validAuthority();

        List<FlashSale> flashSaleList = flashSaleRepository.findAllByEndDateGreaterThanEqualAndIsDeletedFalse(LocalDate.now());

        return flashSaleList.stream().map(flashSaleMapper::convertToResponseDto).toList();
    }

    public List<FlashSaleResponseDto> getList() {
        validAuthority();

        List<FlashSale> flashSaleList = flashSaleRepository.findAllByIsDeletedFalse();

        return flashSaleList.stream().map(flashSaleMapper::convertToResponseDto).toList();
    }

    public FlashSaleResponseDto getOne(UUID flashSaleId) {
        validAuthority();

        FlashSale flashSale = existFlashSale(flashSaleId);

        return flashSaleMapper.convertToResponseDto(flashSale);
    }

    @Transactional
    public String delete(UUID flashSaleId) {
        validAdmin();

        FlashSale flashSale = existFlashSale(flashSaleId);
        flashSale.delete();

        return "삭제되었습니다.";
    }

    protected FlashSale existFlashSale(UUID flashSaleId) {
        return flashSaleRepository.findByIdAndIsDeletedFalse(flashSaleId).orElseThrow(
            () -> new CustomException(FlashSaleErrorCode.NOT_FOUND)
        );
    }

    private void validDuplicateDate(FlashSaleRequestDto flashSaleRequestDto) {
        if (flashSaleRepository.findByStartDateAndEndDateAndIsDeletedFalse(flashSaleRequestDto.startDate(), flashSaleRequestDto.endDate()).isPresent()) {
            throw new CustomException(FlashSaleErrorCode.DUPLICATE_DATE);
        }
    }

    private void validAvailableDate(FlashSaleRequestDto flashSaleRequestDto) {
        if (flashSaleRequestDto.endDate().isBefore(flashSaleRequestDto.startDate())) {
            throw new CustomException(FlashSaleErrorCode.NOT_AVAILABLE_DATE);
        }
    }

    private boolean isOnSale(FlashSale flashSale) {
        LocalDate currentDate = LocalDate.now();
        return (currentDate.isAfter(flashSale.getStartDate()) || currentDate.isEqual(flashSale.getStartDate()))
            && (currentDate.isBefore(flashSale.getEndDate()) || currentDate.isEqual(flashSale.getEndDate()));
    }

    private String getAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities()
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new CustomException(FlashSaleErrorCode.INVALID_PERMISSION_REQUEST))
            .getAuthority();
    }

    private void validAuthority() {
        String authority = getAuthority();

        if (authority.equals("ROLE_CUSTOMER")) {
            throw new CustomException(FlashSaleErrorCode.INVALID_PERMISSION_REQUEST);
        }
    }

    private void validAdmin() {
        String authority = getAuthority();

        if (!(authority.equals("ROLE_MASTER") || authority.equals("ROLE_MANAGER"))) {
            throw new CustomException(FlashSaleErrorCode.INVALID_PERMISSION_REQUEST);
        }
    }
}