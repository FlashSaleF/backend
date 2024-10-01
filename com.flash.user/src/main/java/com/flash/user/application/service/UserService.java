package com.flash.user.application.service;

import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.mapper.UserMapper;
import com.flash.user.domain.model.User;
import com.flash.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JoinResponseDto saveUser(JoinRequestDto joinRequestDto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(joinRequestDto.email())) {
            log.error("email already exists");
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        // 전화번호 중복 체크
        if (userRepository.existsByPhone(joinRequestDto.phone())) {
            log.error("phone already exists");
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다.");
        }

        User saved = userRepository.save(UserMapper.entityFrom(joinRequestDto.withPassword(passwordEncoder.encode(joinRequestDto.password()))));
        saved.setCreatedBy(String.valueOf(saved.getId()));

        return UserMapper.dtoFrom(saved);
    }

}