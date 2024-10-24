package com.flash.user.infrastructure.repository;

import com.flash.user.domain.model.User;
import com.flash.user.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

}
