package com.flash.flashsale.application.service;

import com.flash.flashsale.application.dto.mapper.FlashSaleMapper;
import com.flash.flashsale.application.dto.mapper.FlashSaleProductMapper;
import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.dto.response.InternalProductResponseDto;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;
import com.flash.flashsale.domain.repository.FlashSaleProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public FlashSaleProductResponseDto update(UUID flashSaleProductId, FlashSaleProductRequestDto flashSaleProductRequestDto) {
        validDuplicate(flashSaleProductRequestDto);
        validAvailableDateTime(flashSaleProductRequestDto);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProductRequestDto.flashSaleId());

        validAvailableSailTime(flashSale, flashSaleProductRequestDto);

        FlashSaleProduct flashSaleProduct = existFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.PENDING, FlashSaleProductStatus.APPROVE)).orElseThrow(
            () -> new IllegalArgumentException("승인 중이거나 승인 대기중인 플래시 세일 상품만 수정 할 수 있습니다.")
        );

        flashSaleProduct.update(flashSale, flashSaleProductRequestDto);

        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
    }

    public FlashSaleProductResponseDto getOne(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = existFlashSaleProduct(flashSaleProductId);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
    }

    public List<FlashSaleProductResponseDto> getList(UUID flashSaleId, List<FlashSaleProductStatus> statusList) {
        List<FlashSaleProduct> flashSaleProductList;

        if (flashSaleId == null && statusList == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByIsDeletedFalse();
        } else if (flashSaleId == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByStatusInAndIsDeletedFalse(statusList);
        } else if (statusList == null) {
            flashSaleProductList = flashSaleProductRepository.findAllByFlashSaleIdAndIsDeletedFalse(flashSaleId);
        } else {
            flashSaleProductList = flashSaleProductRepository.findAllByFlashSaleIdAndStatusInAndIsDeletedFalse(flashSaleId, statusList);
        }

        return flashSaleProductList.stream().map(flashSaleProduct ->
        {
            FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
            FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

            return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
        }).toList();
    }

    public List<FlashSaleProductResponseDto> getListByTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<FlashSaleProduct> flashSaleProductList = flashSaleProductRepository.findAllByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsDeletedFalse(endTime, startTime);

        return flashSaleProductList.stream().map(flashSaleProduct ->
        {
            FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
            FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

            return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto);
        }).toList();
    }

    @Transactional
    public String approve(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = existFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.PENDING)).orElseThrow(
            () -> new IllegalArgumentException("승인 대기중인 플래시 세일 상품만 승인 할 수 있습니다.")
        );

        flashSaleProduct.approve();

        return "승인되었습니다.";
    }

    @Transactional
    public String endSale(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = existFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.ONSALE)).orElseThrow(
            () -> new IllegalArgumentException("세일중인 플래시 세일 상품만 종료 할 수 있습니다.")
        );

        flashSaleProduct.endSale();

        return "세일이 종료되었습니다.";
    }

    @Transactional
    @Scheduled(cron = "0 0 10-21 * * *")
    public void autoEndSale() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = currentDateTime.minusMinutes(5);
        LocalDateTime fiveMinutesLater = currentDateTime.plusMinutes(5);
        //실행시간에 따른 오차에 대응하기 위해 임의로 5분씩 설정하였습니다.

        flashSaleProductRepository.findAllByStatusAndEndTimeBetweenAndIsDeletedFalse(FlashSaleProductStatus.ONSALE, fiveMinutesAgo, fiveMinutesLater).forEach(FlashSaleProduct::endSale);
    }

    @Transactional
    @Scheduled(cron = "0 0 09-20 * * *")
    public void autoStartSale() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = currentDateTime.minusMinutes(5);
        LocalDateTime fiveMinutesLater = currentDateTime.plusMinutes(5);
        //실행시간에 따른 오차에 대응하기 위해 임의로 5분씩 설정하였습니다.

        flashSaleProductRepository.findAllByStatusAndEndTimeBetweenAndIsDeletedFalse(FlashSaleProductStatus.APPROVE, fiveMinutesAgo, fiveMinutesLater).forEach(FlashSaleProduct::oneSale);
    }

    private FlashSaleProduct existFlashSaleProduct(UUID flashSaleProductId) {
        return flashSaleProductRepository.findByIdAndIsDeletedFalse(flashSaleProductId).orElseThrow(
            () -> new IllegalArgumentException("존재하지 않는 플래시 세일 상품 입니다.")
        );
    }

    private Optional<FlashSaleProduct> existFlashSaleProductByStatus(UUID flashSaleProductId, List<FlashSaleProductStatus> statusList) {
        return flashSaleProductRepository.findByIdAndStatusInAndIsDeletedFalse(flashSaleProductId, statusList);
    }

    private void validDuplicate(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRepository.findByFlashSaleIdAndProductIdAndIsDeletedFalse(flashSaleProductRequestDto.flashSaleId(), flashSaleProductRequestDto.productId()).isPresent()) {
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

    public InternalProductResponseDto getOneByProductId(UUID productId) {
        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.findByProductIdAndStatusAndIsDeletedFalse(productId, FlashSaleProductStatus.ONSALE);
        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSaleProduct.getFlashSale());

        return flashSaleProductMapper.convertToInternalProductResponseDto(flashSaleProduct, flashSaleResponseDto);
    }

    public List<InternalProductResponseDto> getListByProductIds(List<UUID> productIds) {
        List<FlashSaleProduct> flashSaleProductList = flashSaleProductRepository.findAllByProductIdInAndStatusAndIsDeletedFalse(productIds, FlashSaleProductStatus.ONSALE);

        return flashSaleProductList.stream().map(flashSaleProduct ->
        {
            FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
            FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

            return flashSaleProductMapper.convertToInternalProductResponseDto(flashSaleProduct, flashSaleResponseDto);
        }).toList();
    }
}
