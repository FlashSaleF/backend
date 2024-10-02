package com.flash.auth.application.service.util;

import com.flash.auth.application.dto.response.LoginResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public static LoginResponseDto addToken(LoginResponseDto loginResponseDto, String access, String refresh) {
        return loginResponseDto
                .withAccessToken(access)
                .withRefreshToken(refresh);
    }
}
