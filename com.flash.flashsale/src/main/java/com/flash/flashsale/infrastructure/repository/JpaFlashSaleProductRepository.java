package com.flash.flashsale.infrastructure.repository;

import com.flash.flashsale.domain.model.FlashSaleProduct;
import com.flash.flashsale.domain.repository.FlashSaleProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaFlashSaleProductRepository extends JpaRepository<FlashSaleProduct, UUID>, FlashSaleProductRepository {
}
