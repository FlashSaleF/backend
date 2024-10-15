package com.flash.user.presentation.controller;

import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.request.LoginRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.dto.response.LoginResponseDto;
import com.flash.user.application.dto.response.UserResponseDto;
import com.flash.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "User Internal Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/users")
public class UserInternalController {

    private final UserService userService;

    @PostMapping("/save")
    public JoinResponseDto saveUser(@RequestBody JoinRequestDto joinRequestDto) {
        log.info("Saving user: {}", joinRequestDto.email());
        return userService.saveUser(joinRequestDto);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserInfo(@PathVariable String userId) {
        log.info("Getting user: {}", userId);
        return userService.getUserInfoForAuth(userId);
    }

    @PostMapping("/verify")
    public LoginResponseDto verify(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.verify(loginRequestDto);
    }
}
