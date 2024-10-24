package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashSaleProductRepository {
    FlashSaleProduct save(FlashSaleProduct flashSaleProduct);

    Optional<FlashSaleProduct> findByFlashSaleIdAndProductIdAndIsDeletedFalse(UUID flashSaleId, UUID productId);

    List<FlashSaleProduct> findAllByFlashSaleIdAndStatusInAndIsDeletedFalse(UUID flashSaleId, List<FlashSaleProductStatus> status);

    List<FlashSaleProduct> findAllByIsDeletedFalse();

    List<FlashSaleProduct> findAllByStatusInAndIsDeletedFalse(List<FlashSaleProductStatus> status);

    List<FlashSaleProduct> findAllByFlashSaleIdAndIsDeletedFalse(UUID flashSaleId);

    Optional<FlashSaleProduct> findByIdAndStatusInAndIsDeletedFalse(UUID flashSaleProductId, List<FlashSaleProductStatus> status);

    Optional<FlashSaleProduct> findByIdAndIsDeletedFalse(UUID flashSaleProductId);

    List<FlashSaleProduct> findAllByStatusAndEndTimeBetweenAndIsDeletedFalse(FlashSaleProductStatus flashSaleProductStatus, LocalDateTime fiveMinutesAgo, LocalDateTime fiveMinutesLater);

    List<FlashSaleProduct> findAllByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsDeletedFalse(LocalDateTime endTime, LocalDateTime startTime);

    FlashSaleProduct findByProductIdAndStatusAndIsDeletedFalse(UUID productId, FlashSaleProductStatus flashSaleProductStatus);

    List<FlashSaleProduct> findAllByProductIdInAndStatusAndIsDeletedFalse(List<UUID> productIds, FlashSaleProductStatus flashSaleProductStatus);

    List<FlashSaleProduct> findAllByCreatedByAndIsDeletedFalse(String currentUserId);

    List<FlashSaleProduct> findAllByCreatedByAndStatusInAndIsDeletedFalse(String currentUserId, List<FlashSaleProductStatus> statusList);

    List<FlashSaleProduct> findAllByCreatedByAndFlashSaleIdAndIsDeletedFalse(String currentUserId, UUID flashSaleId);

    List<FlashSaleProduct> findAllByCreatedByAndFlashSaleIdAndStatusInAndIsDeletedFalse(String currentUserId, UUID flashSaleId, List<FlashSaleProductStatus> statusList);

    List<FlashSaleProduct> findAllByCreatedByAndStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndIsDeletedFalse(String currentUserId, LocalDateTime endTime, LocalDateTime startTime);

    boolean existsByFlashSaleIdAndIsDeletedFalse(UUID flashSaleId);

    List<FlashSaleProduct> findAllByProductIdAndIsDeletedFalse(UUID productId);

    List<FlashSaleProduct> findAllByStatusAndStartTimeBetweenAndIsDeletedFalse(FlashSaleProductStatus flashSaleProductStatus, LocalDateTime fiveMinutesAgo, LocalDateTime fiveMinutesLater);
}
