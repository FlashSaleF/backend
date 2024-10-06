package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.ProductMapper;
import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.request.ProductStatusUpdateDto;
import com.flash.vendor.application.dto.request.ProductUpdateRequestDto;
import com.flash.vendor.application.dto.response.*;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorService vendorService;
    private final FeignClientService feignClientService;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request) {

        Vendor vendor = vendorService.getVendorBasedOnAuthority(
                request.vendorId(), getCurrentUserAuthority());

        Product product = Product.createProduct(
                request.name(),
                request.price(),
                request.stock(),
                updateStatusBasedOnStock(request.stock()),
                vendor.getId(),
                request.description()
        );

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponseDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(UUID productId) {

        Product product = productRepository.findByIdAndIsDeletedFalse(productId);

        if (ProductStatus.ON_SALE.equals(product.getStatus())) {
            FlashSaleProductResponseDto flashSaleProductInfo =
                    feignClientService.getFlashSaleProductInfo(productId);
            return ProductMapper.toResponseDtoWithFlashSale(product, flashSaleProductInfo);
        }

        return ProductMapper.toResponseDto(product);
    }

    @Transactional(readOnly = true)
    public ProductPageResponseDto getProducts(Pageable pageable) {

        Page<Product> products = productRepository.findAllByIsDeletedFalse(pageable);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(products);

        return getProductPageResponseDto(products, saleProductListMap);
    }

    @Transactional(readOnly = true)
    public ProductListResponseDto getProductsByIds(List<UUID> productIds) {

        List<Product> productList = productRepository.findAllById(productIds);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(productList);

        return getProductListResponseDto(productList, saleProductListMap);
    }

    @Transactional(readOnly = true)
    public ProductPageResponseDto searchProducts(
            String name, Integer lprice, Integer hprice, String status, Pageable pageable
    ) {

        Page<Product> products =
                productRepository.searchProductsByFilters(
                        name, lprice, hprice, ProductStatus.fromString(status), pageable);

        Map<UUID, FlashSaleProductResponseDto> saleProductListMap =
                getSaleProductListMap(products);

        return getProductPageResponseDto(products, saleProductListMap);
    }

    @Transactional
    public ProductResponseDto updateProduct(
            UUID productId, ProductUpdateRequestDto request
    ) {

        //TODO 캐시 업데이트 AND.. 어디에 영향을 미칠까?
        Product product = validateUserPermission(productId);

        Product updatedProduct = product.updateProduct(
                request.name(),
                request.price(),
                request.stock(),
                request.description()
        );

        return ProductMapper.toResponseDto(updatedProduct);
    }

    @Transactional
    public ProductResponseDto updateProductStatus(
            UUID productId, ProductStatusUpdateDto request
    ) {

        //TODO 캐시 업데이트 AND.. 어디에 영향을 미칠까?
        Product product = validateUserPermission(productId);

        Product updatedProduct = product.updateProductStatus(request.status());

        return ProductMapper.toResponseDto(updatedProduct);
    }

    @Transactional
    public ProductDeleteResponseDto deleteProduct(UUID productId) {

        Product product = validateUserPermission(productId);

        product.delete();

        return new ProductDeleteResponseDto("상품 삭제 성공");
    }

    private Product validateUserPermission(UUID productId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId);

        Vendor vendor = vendorService.getVendorBasedOnAuthority(
                product.getVendorId(), getCurrentUserAuthority());

        if (!product.getVendorId().equals(vendor.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "해당 업체의 상품만 수정할 수 있습니다.");
        }
        return product;
    }

    private Map<UUID, FlashSaleProductResponseDto> getSaleProductListMap(
            Iterable<Product> products
    ) {
        List<UUID> saleProductIds =
                StreamSupport.stream(products.spliterator(), false)
                        .filter(product -> ProductStatus.ON_SALE.equals(product.getStatus()))
                        .map(Product::getId)
                        .toList();

        return saleProductIds.isEmpty()
                ? Collections.emptyMap()
                : feignClientService.getFlashSaleProductListMap(saleProductIds);
    }

    private ProductPageResponseDto getProductPageResponseDto(
            Page<Product> products,
            Map<UUID, FlashSaleProductResponseDto> saleProductListMap
    ) {
        List<ProductResponseDto> productResponseDtos =
                ProductMapper.toResponseDtoList(products, saleProductListMap);

        return new ProductPageResponseDto(new PageImpl<>(
                productResponseDtos, products.getPageable(), products.getTotalElements()));
    }

    private ProductListResponseDto getProductListResponseDto(
            List<Product> products,
            Map<UUID, FlashSaleProductResponseDto> saleProductListMap
    ) {
        List<ProductResponseDto> productResponseDtos =
                ProductMapper.toResponseDtoList(products, saleProductListMap);

        return new ProductListResponseDto(productResponseDtos);
    }

    private ProductStatus updateStatusBasedOnStock(Integer stock) {
        if (stock == null || stock == 0) {
            return ProductStatus.OUT_OF_STOCK;
        } else {
            return ProductStatus.AVAILABLE;
        }
    }

    private String getCurrentUserAuthority() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(BAD_REQUEST, "권한이 존재하지 않습니다."))
                .getAuthority();
    }
}
