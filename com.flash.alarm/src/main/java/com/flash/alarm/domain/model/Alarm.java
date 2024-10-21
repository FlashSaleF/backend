package com.flash.alarm.domain.model;

import com.flash.base.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted is FALSE")
@Table(name = "p_alarms")
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false) // 사용자 이메일
    private String userEmail;

    @Column(nullable = false)
    private UUID flashSaleProductId;

    @Column(nullable = false)
    private UUID flashSaleId;

    @Column(nullable = false)
    private String flashSaleProductName;
}
