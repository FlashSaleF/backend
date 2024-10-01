package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSale;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface FlashSaleRepository {
    FlashSale save(FlashSale flashSale);
    Optional<FlashSale> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);

    Optional<FlashSale> findById(UUID id);
}
