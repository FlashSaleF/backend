package com.flash.flashsale.application.service;

import com.flash.base.exception.CustomException;
import com.flash.flashsale.application.dto.mapper.FlashSaleMapper;
import com.flash.flashsale.application.dto.mapper.FlashSaleProductMapper;
import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.response.FlashSaleProductResponseDto;
import com.flash.flashsale.application.dto.response.FlashSaleResponseDto;
import com.flash.flashsale.application.dto.response.InternalProductResponseDto;
import com.flash.flashsale.application.dto.response.ProductResponseDto;
import com.flash.flashsale.domain.exception.FlashSaleProductErrorCode;
import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;
import com.flash.flashsale.domain.repository.FlashSaleProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashSaleProductService {

    private final FlashSaleService flashSaleService;
    private final FeignClientService feignClientService;

    private final FlashSaleProductMapper flashSaleProductMapper;
    private final FlashSaleMapper flashSaleMapper;

    private final FlashSaleProductRepository flashSaleProductRepository;

    @Transactional
    public FlashSaleProductResponseDto create(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        validVendor();
        validDuplicate(flashSaleProductRequestDto);
        validAvailableDateTime(flashSaleProductRequestDto);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProductRequestDto.flashSaleId());

        validAvailableSailTime(flashSale, flashSaleProductRequestDto);

        FlashSaleProduct flashSaleProduct = flashSaleProductRepository.save(FlashSaleProduct.create(flashSale, flashSaleProductRequestDto));

        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        feignClientService.decreaseProductStock(flashSaleProductRequestDto.productId(), flashSaleProductRequestDto.stock());

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto, feignClientService.getProductInfo(flashSaleProduct.getProductId()));
    }

    @Transactional
    public FlashSaleProductResponseDto update(UUID flashSaleProductId, FlashSaleProductRequestDto flashSaleProductRequestDto) {
        validAuthority();
        validAvailableDateTime(flashSaleProductRequestDto);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProductRequestDto.flashSaleId());

        validAvailableSailTime(flashSale, flashSaleProductRequestDto);

        FlashSaleProduct flashSaleProduct = getFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.PENDING, FlashSaleProductStatus.APPROVE)).orElseThrow(
            () -> new CustomException(FlashSaleProductErrorCode.NOT_AVAILABLE_UPDATE)
        );

        validAvailableFlashSale(flashSaleProduct);

        flashSaleProduct.update(flashSaleProductRequestDto);

        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto, feignClientService.getProductInfo(flashSaleProduct.getProductId()));
    }

    @Transactional(readOnly = true)
    public FlashSaleProductResponseDto getOne(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = getAvailableFlashSaleProduct(flashSaleProductId);

        FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
        FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);

        return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto, feignClientService.getProductInfo(flashSaleProduct.getProductId()));
    }

    @Transactional(readOnly = true)
    public List<FlashSaleProductResponseDto> getList(UUID flashSaleId, List<FlashSaleProductStatus> statusList) {
        List<FlashSaleProduct> flashSaleProductList = getAvailableFlashSaleProductList(flashSaleId, statusList);
        return getFlashSaleProductDtoList(flashSaleProductList);
    }

    @Transactional(readOnly = true)
    public List<FlashSaleProductResponseDto> getListByTime(LocalDateTime startTime, LocalDateTime endTime) {
        List<FlashSaleProduct> flashSaleProductList = getAvailableFlashSaleProductListByTime(startTime, endTime);
        return getFlashSaleProductDtoList(flashSaleProductList);
    }

    private List<FlashSaleProductResponseDto> getFlashSaleProductDtoList(List<FlashSaleProduct> flashSaleProductList) {
        Map<UUID, ProductResponseDto> productListMap = getProductListMap(flashSaleProductList);

        return flashSaleProductList.stream().map(flashSaleProduct ->
        {
            FlashSale flashSale = flashSaleService.existFlashSale(flashSaleProduct.getFlashSale().getId());
            FlashSaleResponseDto flashSaleResponseDto = flashSaleMapper.convertToResponseDto(flashSale);
            ProductResponseDto productResponseDto = productListMap.get(flashSaleProduct.getProductId());

            return flashSaleProductMapper.convertToResponseDto(flashSaleProduct, flashSaleResponseDto, productResponseDto);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<FlashSaleProductResponseDto> getOnSaleList() {
        List<FlashSaleProduct> flashSaleProductList = flashSaleProductRepository.findAllByStatusInAndIsDeletedFalse(List.of(FlashSaleProductStatus.ONSALE));
        return getFlashSaleProductDtoList(flashSaleProductList);
    }

    @Transactional
    public String approve(UUID flashSaleProductId) {
        validAdmin();

        FlashSaleProduct flashSaleProduct = getFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.PENDING)).orElseThrow(
            () -> new CustomException(FlashSaleProductErrorCode.IS_NOT_PENDING)
        );

        flashSaleProduct.approve();

        return "승인 되었습니다.";
    }

    @Transactional
    public String refuse(UUID flashSaleProductId) {
        validAdmin();

        FlashSaleProduct flashSaleProduct = getFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.PENDING, FlashSaleProductStatus.APPROVE)).orElseThrow(
            () -> new CustomException(FlashSaleProductErrorCode.NOT_AVAILABLE_REFUSE)
        );

        flashSaleProduct.refuse();

        return "승인 거절 되었습니다.";
    }

    @Transactional
    public String endSale(UUID flashSaleProductId) {
        validAuthority();

        FlashSaleProduct flashSaleProduct = getFlashSaleProductByStatus(flashSaleProductId, List.of(FlashSaleProductStatus.ONSALE)).orElseThrow(
            () -> new CustomException(FlashSaleProductErrorCode.IS_ON_SALE_DELETE)
        );

        validAvailableFlashSale(flashSaleProduct);

        flashSaleProduct.endSale();

        return "세일이 종료되었습니다.";
    }

    @Transactional
    public String delete(UUID flashSaleId) {
        validAuthority();

        FlashSaleProduct flashSaleProduct = availableFlashSaleProduct(flashSaleId);
        flashSaleProduct.delete();

        return "삭제되었습니다.";
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
            () -> new CustomException(FlashSaleProductErrorCode.NOT_FOUND)
        );
    }

    private FlashSaleProduct getAvailableFlashSaleProduct(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = existFlashSaleProduct(flashSaleProductId);

        if (getAuthority().equals("ROLE_VENDOR")) {
            if (!(flashSaleProduct.getCreatedBy().equals(getCurrentUserId()) || flashSaleProduct.getStatus().equals(FlashSaleProductStatus.ONSALE))) {
                throw new CustomException(FlashSaleProductErrorCode.IS_NOT_ON_SALE_OR_MY_ITEM);
            }
        }

        if (getAuthority().equals("ROLE_CUSTOMER")) {
            if (!flashSaleProduct.getStatus().equals(FlashSaleProductStatus.ONSALE)) {
                throw new CustomException(FlashSaleProductErrorCode.IS_NOT_ON_SALE);
            }
        }

        return flashSaleProduct;
    }

    private List<FlashSaleProduct> getAvailableFlashSaleProductList(UUID flashSaleId, List<FlashSaleProductStatus> statusList) {

        if (flashSaleId == null && statusList == null) {
            if (getAuthority().equals("ROLE_MASTER") || getAuthority().equals("ROLE_MANAGER")) {
                return flashSaleProductRepository.findAllByIsDeletedFalse();
            }
            return flashSaleProductRepository.findAllByCreatedByAndIsDeletedFalse(getCurrentUserId());
        } else if (flashSaleId == null) {
            if (getAuthority().equals("ROLE_MASTER") || getAuthority().equals("ROLE_MANAGER")) {
                return flashSaleProductRepository.findAllByStatusInAndIsDeletedFalse(statusList);
            }
            return flashSaleProductRepository.findAllByCreatedByAndStatusInAndIsDeletedFalse(getCurrentUserId(), statusList);
        } else if (statusList == null) {
            if (getAuthority().equals("ROLE_MASTER") || getAuthority().equals("ROLE_MANAGER")) {
                return flashSaleProductRepository.findAllByFlashSaleIdAndIsDeletedFalse(flashSaleId);
            }
            return flashSaleProductRepository.findAllByCreatedByAndFlashSaleIdAndIsDeletedFalse(getCurrentUserId(), flashSaleId);
        } else {
            if (getAuthority().equals("ROLE_MASTER") || getAuthority().equals("ROLE_MANAGER")) {
                return flashSaleProductRepository.findAllByFlashSaleIdAndStatusInAndIsDeletedFalse(flashSaleId, statusList);
            }
            return flashSaleProductRepository.findAllByCreatedByAndFlashSaleIdAndStatusInAndIsDeletedFalse(getCurrentUserId(), flashSaleId, statusList);
        }
    }

    private List<FlashSaleProduct> getAvailableFlashSaleProductListByTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (getAuthority().equals("ROLE_VENDOR")) {
            return flashSaleProductRepository.findAllByCreatedByAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsDeletedFalse(getCurrentUserId(), endTime, startTime);
        }
        return flashSaleProductRepository.findAllByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsDeletedFalse(endTime, startTime);
    }

    private FlashSaleProduct availableFlashSaleProduct(UUID flashSaleProductId) {
        if (getAuthority().equals("ROLE_MASTER") || getAuthority().equals("ROLE_MANAGER")) {
            return existFlashSaleProduct(flashSaleProductId);
        }

        FlashSaleProduct flashSaleProduct = existFlashSaleProduct(flashSaleProductId);

        if (flashSaleProduct.getCreatedBy().equals(getCurrentUserId())) {
            throw new CustomException(FlashSaleProductErrorCode.IS_NOT_MY_ITEM);
        }

        return flashSaleProduct;
    }

    private Optional<FlashSaleProduct> getFlashSaleProductByStatus(UUID flashSaleProductId, List<FlashSaleProductStatus> statusList) {
        return flashSaleProductRepository.findByIdAndStatusInAndIsDeletedFalse(flashSaleProductId, statusList);
    }

    private Map<UUID, ProductResponseDto> getProductListMap(List<FlashSaleProduct> flashSaleProductList) {
        List<UUID> productIdList = flashSaleProductList.stream().map(FlashSaleProduct::getProductId).distinct().toList();

        return feignClientService.getProductInfoListMap(productIdList);
    }

    private void validAvailableFlashSale(FlashSaleProduct flashSaleProduct) {
        if (getAuthority().equals("ROLE_VENDOR")) {
            if (flashSaleProduct.getCreatedBy().equals(getCurrentUserId())) {
                throw new CustomException(FlashSaleProductErrorCode.IS_NOT_MY_ITEM);
            }
        }
    }

    private void validDuplicate(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRepository.findByFlashSaleIdAndProductIdAndIsDeletedFalse(flashSaleProductRequestDto.flashSaleId(), flashSaleProductRequestDto.productId()).isPresent()) {
            throw new CustomException(FlashSaleProductErrorCode.DUPLICATE);
        }
    }

    private void validAvailableDateTime(FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRequestDto.endTime().isBefore(flashSaleProductRequestDto.startTime())) {
            throw new CustomException(FlashSaleProductErrorCode.NOT_AVAILABLE_DATE);
        }
    }

    private void validAvailableSailTime(FlashSale flashSale, FlashSaleProductRequestDto flashSaleProductRequestDto) {
        if (flashSaleProductRequestDto.startTime().toLocalDate().isBefore(flashSale.getStartDate()) || flashSaleProductRequestDto.endTime().toLocalDate().isAfter(flashSale.getEndDate())) {
            throw new CustomException(FlashSaleProductErrorCode.IS_NOT_ON_SALE_TIME);
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

    private String getAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities()
            .stream()
            .findFirst()
            .orElseThrow(() ->
                new CustomException(FlashSaleProductErrorCode.INVALID_PERMISSION_REQUEST))
            .getAuthority();
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return ((User) authentication.getPrincipal()).getUsername();
    }

    private void validAuthority() {
        String authority = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().toString();

        if (authority.equals("ROLE_CUSTOMER")) {
            throw new CustomException(FlashSaleProductErrorCode.INVALID_PERMISSION_REQUEST);
        }
    }

    private void validAdmin() {
        String authority = getAuthority();

        if (!(authority.equals("ROLE_MASTER") || authority.equals("ROLE_MANAGER"))) {
            throw new CustomException(FlashSaleProductErrorCode.INVALID_PERMISSION_REQUEST);
        }
    }

    private void validVendor() {
        String authority = getAuthority();

        if (!authority.equals("ROLE_VENDOR")) {
            throw new CustomException(FlashSaleProductErrorCode.INVALID_PERMISSION_REQUEST);
        }
    }

    @Transactional
    public void increaseStock(UUID flashSaleProductId) {
        FlashSaleProduct flashSaleProduct = existFlashSaleProduct(flashSaleProductId);

        flashSaleProduct.increaseStock();
    }
}
