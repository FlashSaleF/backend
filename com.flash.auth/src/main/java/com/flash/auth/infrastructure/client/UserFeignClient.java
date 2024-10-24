package com.flash.auth.infrastructure.client;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.request.LoginRequestDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import com.flash.auth.application.service.FeignClientService;
import com.flash.auth.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user", configuration = FeignConfig.class)
public interface UserFeignClient extends FeignClientService {
    @PostMapping("/api/internal/users/save")
    JoinResponseDto saveUser(@RequestBody JoinRequestDto joinRequestDto);

    @PostMapping("/api/internal/users/verify")
    LoginResponseDto verifyUserCredentials(@RequestBody LoginRequestDto loginRequestDto);
}
