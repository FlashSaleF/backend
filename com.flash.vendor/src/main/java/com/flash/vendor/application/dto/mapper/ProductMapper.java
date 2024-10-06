package com.flash.vendor.application.dto.mapper;

import com.flash.vendor.application.dto.response.FlashSaleProductResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
public class ProductMapper {
    public static ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getDescription(),
                product.getVendorId()
        );
    }

    public static ProductResponseDto toResponseDtoWithFlashSale(
            Product product, FlashSaleProductResponseDto flashSaleProductResponseDto
    ) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getDescription(),
                product.getVendorId(),
                flashSaleProductResponseDto
        );
    }

    public static List<ProductResponseDto> toResponseDtoList(
            Iterable<Product> products,
            Map<UUID, FlashSaleProductResponseDto> saleProductListMap
    ) {
        return StreamSupport.stream(products.spliterator(), false)
                .map(product -> {
                    FlashSaleProductResponseDto flashSaleProductInfo =
                            saleProductListMap.get(product.getId());

                    // flashSaleProductInfo가 존재할 경우 ResponseDto에 추가
                    return Optional.ofNullable(flashSaleProductInfo)
                            .map(flashSale ->
                                    ProductMapper.toResponseDtoWithFlashSale(product, flashSale))
                            .orElseGet(() -> ProductMapper.toResponseDto(product));
                })
                .toList();
    }
}
