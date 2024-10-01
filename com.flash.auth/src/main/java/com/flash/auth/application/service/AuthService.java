package com.flash.auth.application.service;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.infrastructure.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "Auth Service")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserFeignClient userFeignClient;

    // TODO: 내부로직에 FeignClient요청이 있는데 이곳에 Transactional을 걸어두어도 되나?
    public JoinResponseDto join(JoinRequestDto joinRequestDto) {
        return userFeignClient.saveUser(joinRequestDto);
    }
}
