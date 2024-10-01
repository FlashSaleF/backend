package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlashSaleService {
    private final FlashSaleRepository flashSaleRepository;

    @Transactional
    public FlashSaleResponseDto create(FlashSaleRequestDto flashSaleRequestDto) {
        validAvailableDate(flashSaleRequestDto);
        validDuplicateDate(flashSaleRequestDto);

        FlashSale flashSale = flashSaleRepository.save(FlashSale.create(flashSaleRequestDto));
        return convertResponseDto(flashSale);
    }

    private void validDuplicateDate(FlashSaleRequestDto flashSaleRequestDto) {
        if (flashSaleRepository.findByStartDateAndEndDate(flashSaleRequestDto.startDate(), flashSaleRequestDto.endDate()).isPresent()) {
            throw new IllegalArgumentException("같은 날짜에 진행되는 세일이 있습니다.");
        }
        ;
    }

    private void validAvailableDate(FlashSaleRequestDto flashSaleRequestDto) {
        if (flashSaleRequestDto.endDate().isBefore(flashSaleRequestDto.startDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private FlashSaleResponseDto convertResponseDto(FlashSale flashSale) {
        return new FlashSaleResponseDto(
            flashSale.getId(),
            flashSale.getName(),
            flashSale.getStartDate(),
            flashSale.getEndDate()
        );
    }
}