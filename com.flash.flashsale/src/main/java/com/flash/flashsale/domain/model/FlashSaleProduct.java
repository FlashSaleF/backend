package com.flash.flashsale.domain.model;

import com.flash.base.jpa.BaseEntity;
import com.flash.flashsale.application.dto.request.FlashSaleProductRequestDto;
import com.flash.flashsale.application.dto.request.FlashSaleProductUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_flash_sale_products")
public class FlashSaleProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "flash_sale_id")
    private FlashSale flashSale;

    @Column(nullable = false)
    private UUID productId;
    @Column(nullable = false)
    private Integer salePrice;
    @Column(nullable = false)
    private Integer stock;
    @Enumerated(EnumType.STRING)
    private FlashSaleProductStatus status;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;

    public static FlashSaleProduct create(FlashSale flashSale, FlashSaleProductRequestDto flashSaleProductRequestDto) {
        return FlashSaleProduct.builder()
            .flashSale(flashSale)
            .productId(flashSaleProductRequestDto.productId())
            .salePrice(flashSaleProductRequestDto.salePrice())
            .stock(flashSaleProductRequestDto.stock())
            .status(FlashSaleProductStatus.PENDING)
            .startTime(flashSaleProductRequestDto.startTime())
            .endTime(flashSaleProductRequestDto.endTime())
            .build();
    }

    public void approve() {
        this.status = FlashSaleProductStatus.APPROVE;
    }

    public void endSale() {
        this.status = FlashSaleProductStatus.ENDSALE;
    }

    public void onSale() {
        this.status = FlashSaleProductStatus.ONSALE;
    }

    public void update(FlashSaleProductUpdateRequestDto flashSaleProductUpdateRequestDto) {
        this.salePrice = flashSaleProductUpdateRequestDto.salePrice();
        this.status = FlashSaleProductStatus.PENDING;
        this.startTime = flashSaleProductUpdateRequestDto.startTime();
        this.endTime = flashSaleProductUpdateRequestDto.endTime();
    }

    public void refuse() {
        this.status = FlashSaleProductStatus.REFUSE;
    }

    public void increaseStock() {
        this.stock++;
    }

    public void decreaseStock() {
        this.stock--;
    }
}
