package com.flash.auth.infrastructure.client;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user")
public interface UserFeignClient {
    @PostMapping("/api/internal/users/save")
    JoinResponseDto saveUser(@RequestBody JoinRequestDto joinRequestDto);
}
