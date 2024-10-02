package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.mapper.ProductMapper;
import com.flash.vendor.application.dto.request.ProductRequestDto;
import com.flash.vendor.application.dto.response.ProductResponseDto;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.model.Vendor;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
