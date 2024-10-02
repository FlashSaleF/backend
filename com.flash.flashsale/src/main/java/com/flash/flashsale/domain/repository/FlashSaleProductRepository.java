package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.model.FlashSaleProductStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlashSaleProductRepository {
    FlashSaleProduct save(FlashSaleProduct flashSaleProduct);

    Optional<FlashSaleProduct> findByFlashSaleIdAndProductId(UUID flashSaleId, UUID productId);

    List<FlashSaleProduct> findAllByFlashSaleIdAndStatusAndIsDeletedFalse(UUID flashSaleId, FlashSaleProductStatus status);

    List<FlashSaleProduct> findAllByIsDeletedFalse();

    List<FlashSaleProduct> findAllByStatusAndIsDeletedFalse(FlashSaleProductStatus status);

    List<FlashSaleProduct> findAllByFlashSaleIdAndIsDeletedFalse(UUID flashSaleId);
}
