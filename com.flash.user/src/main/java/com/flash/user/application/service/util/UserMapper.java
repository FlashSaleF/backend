package com.flash.user.application.service.util;

import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.dto.response.LoginResponseDto;
import com.flash.user.application.dto.response.UserInfoResponseDto;
import com.flash.user.application.dto.response.UserResponseDto;
import com.flash.user.domain.model.RoleEnum;
import com.flash.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static User entityFrom(JoinRequestDto dto) {
        return User.builder()
                .email(dto.email())
                .password(dto.password())
                .role(RoleEnum.fromRoleCode(dto.role()))
                .address(dto.address())
                .phone(dto.phone())
                .name(dto.name())
                .build();
    }

    public static JoinResponseDto dtoFrom(User user) {
        return JoinResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getAuthority())
                .name(user.getName())
                .build();
    }

    public static UserResponseDto getInfoFrom(User user) {
        return UserResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .id(user.getId())
                .build();
    }

    public static LoginResponseDto toAuthFrom(User user) {
        return LoginResponseDto.builder()
                .id(String.valueOf(user.getId()))
                .role(user.getRole().getAuthority())
                .build();
    }

    public static UserInfoResponseDto toUserInfoFrom(User user) {
        return UserInfoResponseDto.builder()
                .email(user.getEmail())
                .role(user.getRole().getAuthority())
                .address(user.getAddress())
                .phone(user.getPhone())
                .name(user.getName())
                .build();
    }
}
