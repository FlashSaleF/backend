package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.mapper.FlashSaleMapper;
import com.flash.flashsale.application.dto.mapper.FlashSaleProductMapper;
import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;
import com.flash.flashsale.domain.repository.FlashSaleProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashSaleProductService {

    private final FlashSaleService flashSaleService;

    private final FlashSaleProductMapper flashSaleProductMapper;
    private final FlashSaleMapper flashSaleMapper;
    private final FlashSaleProductRepository flashSaleProductRepository;

    @Transactional
    public FlashSaleProductResponseDto create(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        validDuplicate(flashSaleProductRequestDto);
        validAvailableDateTime(flashSaleProductRequestDto);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProductRequestDto.flashSaleId());

        validAvailableSailTime(flashSale, flashSaleProductRequestDto);

        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.save(FlashSaleProduct.create(flashSale, flashSaleProductRequestDto));

        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
    }

    public List<FlashSaleProductResponseDto> getList(UUID flashSaleId, FlashSaleProductStatus status) {
        List<FlashSaleProduct> flashSaleProductList;

        if (flashSaleId == null && status == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByIsDeletedFalse();
        } else if (flashSaleId == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByStatusAndIsDeletedFalse(status);
        } else if (status == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByFlashSaleIdAndIsDeletedFalse(flashSaleId);
        } else {
            flashSaleProductList = flashSaleProductRepository.findAllByFlashSaleIdAndStatusAndIsDeletedFalse(flashSaleId, status);
        }

        return flashSaleProductList.stream().map(flashSaleProduct ->
        {
            FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
            FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);
            return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
        }).toList();
    }

    private void validDuplicate(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRepository.findByFlashSaleIdAndProductId(flashSaleProductRequestDto.flashSaleId(), flashSaleProductRequestDto.productId()).isPresent()) {
            throw new IllegalArgumentException("동일한 세일 상품이 존재합니다.");
        }
    }

    private void validAvailableDateTime(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRequestDto.endTime().isBefore(flashSaleProductRequestDto.startTime())) {
            throw new IllegalArgumentException("종료시간은 시작시간보다 빠를 수 없습니다.");
        }
    }

    private void validAvailableSailTime(FlashSale flashSale, FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRequestDto.startTime().toLocalDate().isBefore(flashSale.getStartDate()) || flashSaleProductRequestDto.endTime().toLocalDate().isAfter(flashSale.getEndDate())) {
            throw new IllegalArgumentException("해당 플래시 세일이 진행중이지 않은 시간입니다.");
        }
    }
}