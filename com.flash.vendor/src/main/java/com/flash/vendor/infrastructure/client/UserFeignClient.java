package com.flash.vendor.infrastructure.client;

import com.flash.vendor.application.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user")
public interface UserFeignClient {

    @GetMapping("/api/internal/users/{userId}")
    UserResponseDto getUserInfo(@PathVariable String userId);
}
