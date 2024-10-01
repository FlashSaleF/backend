package com.flash.user.domain.repository;

import com.flash.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);
}
