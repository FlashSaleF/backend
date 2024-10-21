package com.flash.alarm.infrastructure.client;

import com.flash.alarm.application.dto.response.UserResponseDto;
import com.flash.alarm.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/internal/users/{userId}")
    UserResponseDto ValidUser(@PathVariable String userId);
}
