package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.mapper.FlashSaleProductMapper;
import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.repository.FlashSaleProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlashSaleProductService {

    private final FlashSaleService flashSaleService;

    private final FlashSaleProductMapper flashSaleProductMapper;
    private final FlashSaleProductRepository flashSaleProductRepository;

    @Transactional
    public FlashSaleProductResponseDto create(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        validDuplicate(flashSaleProductRequestDto);
        validAvailableDateTime(flashSaleProductRequestDto);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProductRequestDto.flashSaleId());

        validAvailableSailTime(flashSale, flashSaleProductRequestDto);

        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.save(FlashSaleProduct.create(flashSale, flashSaleProductRequestDto));

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct);
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
        if ((flashSaleProductRequestDto.startTime().toLocalDate().isAfter(flashSale.getStartDate()) || flashSaleProductRequestDto.startTime().toLocalDate().isEqual(flashSale.getStartDate()))
            && (flashSaleProductRequestDto.endTime().toLocalDate().isBefore(flashSale.getEndDate()) || flashSaleProductRequestDto.endTime().toLocalDate().isEqual(flashSale.getEndDate()))) {
            throw new IllegalArgumentException("해당 플래시 세일이 진행중이지 않은 시간입니다.");
        }
    }
}
