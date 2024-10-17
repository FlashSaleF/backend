package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.ProductMapper;
import com.flash.vendor.application.dto.request.*;
import com.flash.vendor.application.dto.response.*;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductService productService;
    private final VendorService vendorService;
    private final FeignClientService feignClientService;
    private final RedisLockService redisLockService;

    public ProductResponseDto createProduct(ProductRequestDto request) {

        Vendor vendor = vendorService.getVendorBasedOnAuthority(request.vendorId());

        Product savedProduct = productService.createProduct(
                request.name(),
                request.price(),
                request.stock(),
                vendor.getId(),
                request.description()
        );

        return ProductMapper.toResponseDto(savedProduct);
    }

    public ProductResponseDto getProduct(UUID productId) {

        Product product = productService.getProductById(productId);

        if (ProductStatus.ON_SALE.equals(product.getStatus())) {
            FlashSaleProductResponseDto flashSaleProductInfo =
                    feignClientService.getFlashSaleProductInfo(productId);
            return ProductMapper.toResponseDtoWithFlashSale(product, flashSaleProductInfo);
        }

        return ProductMapper.toResponseDto(product);
    }

    public ProductPageResponseDto getProducts(Pageable pageable) {

        Page<Product> products = productService.getAllProducts(pageable);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(products);

        return ProductMapper.toProductPageResponseDto(products, saleProductListMap);
    }

    public ProductListResponseDto getProductsByIds(List<UUID> productIds) {

        List<Product> productList = productService.getProductsByIds(productIds);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(productList);

        return ProductMapper.toProductListResponseDto(productList, saleProductListMap);
    }

    public ProductPageResponseDto searchProducts(
            String name, Integer lprice, Integer hprice, String status, Pageable pageable
    ) {

        Page<Product> products = productService.searchProductsByFilters(
                name, lprice, hprice, status, pageable);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(products);

        return ProductMapper.toProductPageResponseDto(products, saleProductListMap);
    }

    public ProductResponseDto updateProduct(
            UUID productId, ProductUpdateRequestDto request
    ) {

        Product product = validateUserPermission(productId);

        Product updatedProduct = redisLockService
                .lockAndExecute("product_stock_lock:" + productId, () ->
                        productService.updateProduct(
                                product,
                                request.name(),
                                request.price(),
                                request.stock(),
                                request.description()
                        )
                );

        return ProductMapper.toResponseDto(updatedProduct);
    }

    public ProductResponseDto updateProductStatus(
            UUID productId, ProductStatusUpdateDto request
    ) {

        Product product = validateUserPermission(productId);

        Product updatedProduct = redisLockService
                .lockAndExecute("product_stock_lock:" + product.getId(), () ->
                        productService.updateProductStatus(product, request.status())
                );

        return ProductMapper.toResponseDto(updatedProduct);
    }

    public ProductResponseDto updateProductStatusForServer(
            UUID productId, ProductStatusUpdateDto request
    ) {

        Product product = productService.getProductById(productId);

        Product updatedProduct = redisLockService
                .lockAndExecute("product_stock_lock:" + product.getId(), () ->
                        productService.updateProductStatus(product, request.status())
                );

        return ProductMapper.toResponseDto(updatedProduct);
    }

    public ProductDeleteResponseDto deleteProduct(UUID productId) {

        Product product = validateUserPermission(productId);

        productService.deleteProduct(product);

        return new ProductDeleteResponseDto("상품 삭제 성공");
    }

    public ProductStockDecreaseResponseDto decreaseProductStock(
            UUID productId, ProductStockDecreaseRequestDto request
    ) {

        Product updatedProduct = redisLockService
                .lockAndExecute("product_stock_lock:" + productId, () ->
                        productService.decreaseStock(productId, request.quantity())
                );

        return new ProductStockDecreaseResponseDto(
                updatedProduct.getId(), HttpStatus.OK.value());
    }

    public ProductStockIncreaseResponseDto increaseProductStock(
            UUID productId, ProductStockIncreaseRequestDto request
    ) {

        Product updatedProduct = redisLockService.
                lockAndExecute("product_stock_lock:" + productId, () ->
                        productService.increaseStock(productId, request.quantity())
                );

        return new ProductStockIncreaseResponseDto(
                updatedProduct.getId(), HttpStatus.OK.value());
    }

    private Product validateUserPermission(UUID productId) {

        Product product = productService.getProductById(productId);

        Vendor vendor = vendorService.getVendorBasedOnAuthority(product.getVendorId());

        return product;
    }

    private Map<UUID, FlashSaleProductResponseDto> getSaleProductListMap(
            Iterable<Product> products
    ) {
        List<UUID> saleProductIds = productService.getOnSaleProductIds(products);

        return saleProductIds.isEmpty()
                ? Collections.emptyMap()
                : feignClientService.getFlashSaleProductListMap(saleProductIds);
    }
}
