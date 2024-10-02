package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSaleProduct;

import java.util.Optional;
import java.util.UUID;

public interface FlashSaleProductRepository {
    FlashSaleProduct save(FlashSaleProduct flashSaleProduct);

    Optional<FlashSaleProduct> findByFlashSaleIdAndProductId(UUID flashSaleId, UUID productId);
}
