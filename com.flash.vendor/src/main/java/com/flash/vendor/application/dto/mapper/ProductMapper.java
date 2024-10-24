package com.flash.vendor.application.dto.mapper;

import com.flash.vendor.application.dto.ProductSnapshot;
import com.flash.vendor.application.dto.response.FlashSaleProductResponseDto;
import com.flash.vendor.application.dto.response.ProductListResponseDto;
import com.flash.vendor.application.dto.response.ProductPageResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public static ProductPageResponseDto toProductPageResponseDto(
            Page<Product> products,
            Map<UUID, FlashSaleProductResponseDto> saleProductListMap
    ) {
        List<ProductResponseDto> productResponseDtos =
                toResponseDtoList(products, saleProductListMap);

        return new ProductPageResponseDto(new PageImpl<>(
                productResponseDtos, products.getPageable(), products.getTotalElements()));
    }

    public static ProductSnapshot toProductSnapshot(Product product) {
        return new ProductSnapshot(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getDescription(),
                product.isDeleted()
        );
    }

    public static ProductListResponseDto toProductListResponseDto(
            List<Product> products,
            Map<UUID, FlashSaleProductResponseDto> saleProductListMap
    ) {
        List<ProductResponseDto> productResponseDtos =
                toResponseDtoList(products, saleProductListMap);

        return new ProductListResponseDto(productResponseDtos);
    }

    private static List<ProductResponseDto> toResponseDtoList(
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
