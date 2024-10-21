package com.flash.alarm.domain.model;

import com.flash.base.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted is FALSE")
@Table(name = "p_scheduled_tasks")
public class ScheduledTaskEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TaskStatus status = TaskStatus.SCHEDULED;

    @Column(nullable = false)
    private LocalDateTime schedulingTime;

    @Column(nullable = false)
    private UUID flashSaleProductId;

    @Column(nullable = false)
    private UUID flashSaleId;
}
