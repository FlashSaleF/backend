package com.flash.user.domain.repository;

import com.flash.user.domain.model.User;

public interface UserRepository {

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
