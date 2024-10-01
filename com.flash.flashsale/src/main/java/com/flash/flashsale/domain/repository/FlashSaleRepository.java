package com.flash.flashsale.domain.repository;

import com.flash.flashsale.domain.model.FlashSale;

import java.time.LocalDate;
import java.util.Optional;

public interface FlashSaleRepository {
    FlashSale save(FlashSale flashSale);

    Optional<FlashSale> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}