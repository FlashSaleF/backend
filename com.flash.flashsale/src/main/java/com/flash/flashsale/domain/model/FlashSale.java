package com.flash.flashsale.domain.model;

import com.flash.base.jpa.BaseEntity;
import com.flash.flashsale.application.dto.request.FlashSaleRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_flash_sales")
public class FlashSale extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;

    public static FlashSale create(FlashSaleRequestDto flashSaleRequestDto) {
        return FlashSale.builder()
            .name(flashSaleRequestDto.name())
            .startDate(flashSaleRequestDto.startDate())
            .endDate(flashSaleRequestDto.endDate())
            .build();
    }

    public void update(FlashSaleRequestDto flashSaleRequestDto) {
        this.name = flashSaleRequestDto.name();
        this.startDate = flashSaleRequestDto.startDate();
        this.endDate = flashSaleRequestDto.endDate();
    }
}