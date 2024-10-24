package com.flash.vendor.domain.model;

import com.flash.base.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_vendors")
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class Vendor extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    public static Vendor createVendor(Long userId, String username, String name, String address) {
        return Vendor.builder()
                .userId(userId)
                .username(username)
                .name(name)
                .address(address)
                .build();
    }

    public Vendor updateVendor(String name, String address) {
        this.name = name;
        this.address = address;
        return this;
    }

}
