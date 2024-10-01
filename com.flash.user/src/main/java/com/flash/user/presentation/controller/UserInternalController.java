package com.flash.user.presentation.controller;

import com.flash.user.application.dto.request.JoinRequestDto;
import com.flash.user.application.dto.response.JoinResponseDto;
import com.flash.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
