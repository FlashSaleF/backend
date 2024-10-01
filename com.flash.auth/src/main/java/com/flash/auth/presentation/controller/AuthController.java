package com.flash.auth.presentation.controller;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public JoinResponseDto join(@RequestBody @Valid JoinRequestDto joinRequestDto) {
        return authService.join(joinRequestDto);
    }

}
