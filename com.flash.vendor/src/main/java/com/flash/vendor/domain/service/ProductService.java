package com.flash.vendor.domain.service;

import com.flash.base.exception.CustomException;
import com.flash.vendor.domain.exception.ProductErrorCode;
import com.flash.vendor.domain.model.Product;
import com.flash.vendor.domain.model.ProductStatus;
import com.flash.vendor.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(
            String name, Integer price, Integer stock, UUID vendorId, String description
    ) {

        Product product = Product.createProduct(
                name, price, stock, getStatusBasedOnStock(stock), vendorId, description);

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product getProductById(UUID productId) {

        return productRepository.findByIdAndIsDeletedFalse(productId);
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {

        return productRepository.findAllByIsDeletedFalse(pageable);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByIds(List<UUID> productIds) {

        return productRepository.findAllById(productIds);
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProductsByFilters(
            String name, Integer lprice, Integer hprice, String status, Pageable pageable
    ) {

        return productRepository.searchProductsByFilters(
                name, lprice, hprice, ProductStatus.fromString(status), pageable);
    }

    @Transactional
    public Product updateProduct(
            Product product, String name, Integer price, Integer stock, String description
    ) {

        return product.updateProduct(name, price, stock, description);
    }

    @Transactional
    public Product updateProductStatus(Product product, ProductStatus status) {

        return product.updateProductStatus(status);
    }

    @Transactional
    public void deleteProduct(Product product) {

        product.delete();
    }

    @Transactional
    public Product decreaseStock(UUID productId, Integer quantity) {

        Product product = productRepository.findByIdAndIsDeletedFalse(productId);

        if (product.getStock() < quantity) {
            throw new CustomException(ProductErrorCode.INSUFFICIENT_STOCK);
        }

        return product.decreaseProductStock(quantity);
    }

    @Transactional
    public Product increaseStock(UUID productId, Integer quantity) {

        Product product = productRepository.findByIdAndIsDeletedFalse(productId);

        return product.increaseProductStock(quantity);
    }

    public List<UUID> getOnSaleProductIds(Iterable<Product> products) {
        return StreamSupport.stream(products.spliterator(), false)
                .filter(product -> ProductStatus.ON_SALE.equals(product.getStatus()))
                .map(Product::getId)
                .toList();
    }

    private ProductStatus getStatusBasedOnStock(Integer stock) {
        if (stock == null || stock == 0) {
            return ProductStatus.OUT_OF_STOCK;
        } else {
            return ProductStatus.AVAILABLE;
        }
    }
}
