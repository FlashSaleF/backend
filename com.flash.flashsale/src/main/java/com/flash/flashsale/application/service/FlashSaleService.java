package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.mapper.FlashSaleMapper;
import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.repository.FlashSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashSaleService {

    private final FlashSaleMapper flashSaleMapper;
    private final FlashSaleRepository flashSaleRepository;

    @Transactional
    public FlashSaleResponseDto create(FlashSaleRequestDto flashSaleRequestDto) {
        validAvailableDate(flashSaleRequestDto);
        validDuplicateDate(flashSaleRequestDto);

        FlashSale flashSale = flashSaleRepository.save(FlashSale.create(flashSaleRequestDto));
        return flashSaleMapper.convertToResponseDto(flashSale);
    }

    @Transactional
    public FlashSaleResponseDto update(UUID flashSaleId, FlashSaleRequestDto flashSaleRequestDto) {
        validDuplicateDate(flashSaleRequestDto);
        validAvailableDate(flashSaleRequestDto);
        FlashSale flashSale = excistFlashSale(flashSaleId);

        if (isOnSale(flashSale)) {
            throw new IllegalArgumentException("세일중에는 수정할 수 없습니다.");
        }

        flashSale.update(flashSaleRequestDto);

        return flashSaleMapper.convertToResponseDto(flashSale);
    }

    private FlashSale excistFlashSale(UUID flashSaleId) {
        return flashSaleRepository.findById(flashSaleId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 플래시 세일 입니다.")
        );
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

    private boolean isOnSale(FlashSale flashSale) {
        LocalDate currentDate = LocalDate.now();
        return (currentDate.isAfter(flashSale.getStartDate()) || currentDate.isEqual(flashSale.getStartDate()))
            && (currentDate.isBefore(flashSale.getEndDate()) || currentDate.isEqual(flashSale.getEndDate()));
    }
}