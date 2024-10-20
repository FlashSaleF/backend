package com.flash.alarm.infrastructure.client;

import com.flash.alarm.application.dto.response.UserResponseDto;
import com.flash.alarm.application.service.util.UserFeignClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeignClientImpl implements UserFeignClientService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserResponseDto ValidUser(String userId) {
        return userFeignClient.ValidUser(userId);
    }
}
