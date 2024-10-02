package com.flash.vendor.application.dto.mapper;

import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public static ProductResponseDto convertToResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus().name(),
                product.getDescription(),
                product.getVendorId()
        );
    }
}
