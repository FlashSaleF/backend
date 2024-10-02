package com.flash.flashsale.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FlashSaleProductStatus {
    PENDING("PENDING"),
    APPROVE("APPROVE"),
    ONSALE("ONSALE"),
    ENDSALE("ENDSALE");

    private final String status;
}