package com.flash.user.domain.model;

import com.flash.base.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted is FALSE")
@Table(name = "p_users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Setter
    private String address;

    @Setter
    @Column(unique = true, nullable = false)
    private String phone;

    @Setter
    @Column(nullable = false)
    private String name;

}

