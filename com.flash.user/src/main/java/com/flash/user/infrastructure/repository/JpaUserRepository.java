package com.flash.user.infrastructure.repository;

import com.flash.user.domain.model.User;
import com.flash.user.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
