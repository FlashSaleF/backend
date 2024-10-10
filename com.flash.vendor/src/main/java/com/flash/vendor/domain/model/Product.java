package com.flash.vendor.domain.model;

import com.flash.base.exception.CustomException;
import com.flash.base.jpa.BaseEntity;
import com.flash.vendor.domain.exception.ProductErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_products")
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(nullable = false)
    private UUID vendorId;

    private String description;

    public static Product createProduct(String name, int price, int stock, ProductStatus status, UUID vendorId, String description) {
        return Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .status(status)
                .vendorId(vendorId)
                .description(description)
                .build();
    }

    public Product updateProduct(String name, int price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
        return this;
    }

    public Product updateProductStatus(ProductStatus status) {
        this.status = status;
        return this;
    }

    public void decreaseProductStock(Integer quantity) {
        if (quantity <= 0) {
            throw new CustomException(ProductErrorCode.INVALID_QUANTITY);
        }
        if (this.stock < quantity) {
            throw new CustomException(ProductErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }
}
