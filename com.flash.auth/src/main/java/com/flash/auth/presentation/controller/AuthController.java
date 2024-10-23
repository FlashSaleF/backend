package com.flash.auth.presentation.controller;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.request.LoginRequestDto;
import com.flash.auth.application.dto.response.AuthResponseDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import com.flash.auth.application.service.AuthService;
import com.flash.auth.application.service.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.flash.auth.application.service.util.JwtUtil.AUTHORIZATION_HEADER;

@Slf4j(topic = "Auth Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    // @PreAuthorize("isAnonymous()")
    @PostMapping("/join")
    public ResponseEntity<JoinResponseDto> join(@RequestBody @Valid JoinRequestDto joinRequestDto) {
        return ResponseEntity.ok(authService.join(joinRequestDto));
    }

    // @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.attemptAuthentication(loginRequestDto);
        response.addHeader(AUTHORIZATION_HEADER, loginResponseDto.accessToken());
        response.addCookie(cookieUtil.createCookieWithRefreshToken(loginResponseDto.refreshToken()));

        return ResponseEntity.ok(loginResponseDto.id());
    }

    @PostMapping("/verify")
    public AuthResponseDto verify(
            @RequestHeader HttpHeaders headers) {
        log.info("유효한 캐시가 존재할 때 여기로 요청이 오지 않아야 함");
        return authService.verify(headers);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
