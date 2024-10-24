package com.flash.vendor.infrastructure.client.feign;

import com.flash.vendor.application.dto.response.UserResponseDto;
import com.flash.vendor.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/internal/users/{userId}")
    UserResponseDto getUserInfo(@PathVariable String userId);
}
