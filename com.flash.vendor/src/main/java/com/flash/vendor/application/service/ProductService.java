package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.ProductMapper;
import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.response.ProductPageResponseDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorService vendorService;

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

        return ProductMapper.convertToResponseDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(UUID productId) {

        Product product = getProductByIdAndIsDeletedFalse(productId);

        return ProductMapper.convertToResponseDto(product);
    }

    @Transactional(readOnly = true)
    public ProductPageResponseDto getProducts(Pageable pageable) {

        Page<Product> products = productRepository.findAllByIsDeletedFalse(pageable);

        return new ProductPageResponseDto(products.map(ProductMapper::convertToResponseDto));
    }

    private Product getProductBasedOnAuthority(UUID productId) {
        return switch (getCurrentUserAuthority()) {
            case "CUSTOMER", "VENDOR" ->
                    getProductByIdAndIsDeletedFalse(productId);
            case "MANAGER", "MASTER" -> getProductById(productId);
            default ->
                    throw new ResponseStatusException(BAD_REQUEST, "유효하지 않은 권한 요청입니다.");
        };
    }

    private Product getProductByIdAndIsDeletedFalse(UUID productId) {
        return productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 상품이 없습니다."));
    }

    private Product getProductById(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() ->
                new ResponseStatusException(NOT_FOUND, "해당 ID로 등록된 상품이 없습니다."));
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
