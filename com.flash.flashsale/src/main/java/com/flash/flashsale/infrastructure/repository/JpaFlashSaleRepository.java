package com.flash.flashsale.infrastructure.repository;

import com.flash.flashsale.domain.model.FlashSale;
import com.flash.flashsale.domain.repository.FlashSaleRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaFlashSaleRepository extends JpaRepository<FlashSale, UUID>, FlashSaleRepository {
}