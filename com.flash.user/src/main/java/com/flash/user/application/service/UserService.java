package com.flash.user.application.service;

import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.request.LoginRequestDto;
import com.flash.user.application.dto.request.UpdateRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.dto.response.LoginResponseDto;
import com.flash.user.application.dto.response.UserInfoResponseDto;
import com.flash.user.application.dto.response.UserResponseDto;
import com.flash.user.application.service.util.UserMapper;
import com.flash.user.domain.model.User;
import com.flash.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Transactional(readOnly = true)
    public User getUser(String userId) {
        return userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> {
                    log.error("getUser method: user not found by id");
                    throw new IllegalArgumentException("user not found by id");
                }
        );
    }

    public UserResponseDto getUserInfoForVendor(String userId) {
        return UserMapper.getNameFrom(getUser(userId));
    }

    @Transactional(readOnly = true)
    public LoginResponseDto verify(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email()).orElseThrow(() -> {
            // TODO: 커스텀 예외 만들어서 던지기
            log.error("user not found by email");
            throw new IllegalArgumentException("not found");

        });
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            // TODO: 커스텀 예외 만들어서 던지기
            log.error("password does not match");
            throw new IllegalArgumentException("password does not match");
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

        return UserMapper.getNameFrom(user);
    }

    public Page<UserInfoResponseDto> getUserList(int page, int size, String sortBy, boolean isAsc) {
        // 페이징 처리를 하기 위한 Sort 객체 생성
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        // page, size, Sort 객체로 Pageable 객체 완성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> all = userRepository.findAll(pageable);
        if (all.isEmpty()) {
            log.error("user list is empty");
            // TODO: 커스텀 예외 변경
            throw new IllegalArgumentException("user list is empty");
        }

        return all.map(UserMapper::toUserInfoFrom);
    }

}
