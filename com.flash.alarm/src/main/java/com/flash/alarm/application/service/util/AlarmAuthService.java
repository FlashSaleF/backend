package com.flash.alarm.application.service.util;

import com.flash.alarm.application.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j(topic = "Alarm Auth Service")
@Component
@RequiredArgsConstructor
public class AlarmAuthService {

    private final UserFeignClientService userFeignClientService;

    public UserResponseDto verifyIdentity() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("userId: {}", userId);

        return userFeignClientService.ValidUser(userId);
    }
}
