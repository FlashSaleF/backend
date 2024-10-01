package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSale;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FlashSaleRepository {
    FlashSale save(FlashSale flashSale);

    Optional<FlashSale> findByStartDateAndEndDateAndIsDeletedFalse(LocalDate startDate, LocalDate endDate);

    Optional<FlashSale> findByIdAndIsDeletedFalse(UUID id);
}
