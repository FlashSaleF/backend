package com.flash.auth.application.service.util;

import com.flash.auth.application.dto.response.AuthResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import com.flash.base.dto.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public static LoginResponseDto addToken(LoginResponseDto loginResponseDto, String access, String refresh) {
        return loginResponseDto
                .withAccessToken(access)
                .withRefreshToken(refresh);
    }

    public static AuthResponseDto toGateway(String id, String role) {
        return AuthResponseDto.builder()
                .id(id)
                .role(role)
                .build();
    }

    public static UserInfo toUserInfo(String id, String role, String token) {
        return UserInfo.builder()
                .id(id)
                .role(role)
                .token(token)
                .build();
    }
}
