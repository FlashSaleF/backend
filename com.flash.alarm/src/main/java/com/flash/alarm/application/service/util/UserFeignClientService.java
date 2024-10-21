package com.flash.alarm.application.service.util;

import com.flash.alarm.application.dto.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public interface UserFeignClientService {

    UserResponseDto ValidUser(String userId);
}
