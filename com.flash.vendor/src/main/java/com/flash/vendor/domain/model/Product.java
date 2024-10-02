package com.flash.vendor.domain.model;

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
public class Product {

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
}
