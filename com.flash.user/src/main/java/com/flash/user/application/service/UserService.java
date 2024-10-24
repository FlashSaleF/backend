package com.flash.user.application.service;

import com.flash.base.exception.CustomException;
import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.request.LoginRequestDto;
import com.flash.user.application.dto.request.UpdateRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.dto.response.LoginResponseDto;
import com.flash.user.application.dto.response.UserInfoResponseDto;
import com.flash.user.application.dto.response.UserResponseDto;
import com.flash.user.application.service.util.UserMapper;
import com.flash.user.domain.exception.UserErrorCode;
import com.flash.user.domain.model.User;
import com.flash.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

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
            throw new CustomException(UserErrorCode.DUPLICATED_EMAIL);
        }
        // 전화번호 중복 체크
        if (userRepository.existsByPhone(joinRequestDto.phone())) {
            log.error("phone already exists");
            throw new CustomException(UserErrorCode.DUPLICATED_PHONE);
        }

        User saved = userRepository.save(UserMapper.entityFrom(joinRequestDto.withPassword(passwordEncoder.encode(joinRequestDto.password()))));
        saved.setCreatedBy(String.valueOf(saved.getId()));

        return UserMapper.dtoFrom(saved);
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> {
                    log.error("getUser method: user not found by id");
                    throw new CustomException(UserErrorCode.USER_NOT_FOUND);
                }
        );
    }

    public UserResponseDto getUserInfoForAuth(String userId) {
        return UserMapper.getInfoFrom(getUser(userId));
    }

    @Transactional(readOnly = true)
    public LoginResponseDto verify(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email()).orElseThrow(() -> {
            log.error("user not found by email");
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);

        });
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            log.error("password does not match");
            throw new CustomException(UserErrorCode.INVALID_PASSWORD);
        }
        return UserMapper.toAuthFrom(user);
    }

    public UserInfoResponseDto getUserInfo(String userId) {
        return UserMapper.toUserInfoFrom(getUser(userId));
    }

    @Transactional
    public UserInfoResponseDto updateUser(String userId, UpdateRequestDto updateRequestDto) {
        User user = getUser(userId);
        user.setAddress(updateRequestDto.address() != null ? updateRequestDto.address() : user.getAddress());
        user.setPhone(updateRequestDto.phone() != null ? updateRequestDto.phone() : user.getPhone());
        user.setName(updateRequestDto.name() != null ? updateRequestDto.name() : user.getName());
        return UserMapper.toUserInfoFrom(user);
    }

    @Transactional
    public UserResponseDto deleteUser(String userId) {
        User user = getUser(userId);
        user.delete();

        return UserMapper.getInfoFrom(user);
    }

    public Page<UserInfoResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc) {
        // 페이징 처리를 하기 위한 Sort 객체 생성
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        // page, size, Sort 객체로 Pageable 객체 완성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> all = userRepository.findAll(pageable);
        if (all.isEmpty()) {
            log.warn("user list is empty");
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return all.map(UserMapper::toUserInfoFrom);
    }

}
