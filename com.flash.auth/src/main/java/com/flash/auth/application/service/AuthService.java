package com.flash.auth.application.service;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.request.LoginRequestDto;
import com.flash.auth.application.dto.response.AuthResponseDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import com.flash.auth.application.service.util.AuthMapper;
import com.flash.auth.application.service.util.JwtUtil;
import com.flash.auth.domain.exception.AuthErrorCode;
import com.flash.base.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Slf4j(topic = "Auth Service")
@Service
@RequiredArgsConstructor
public class AuthService {

    private final FeignClientService feignClientService;
    private final JwtUtil jwtUtil;

    public JoinResponseDto join(JoinRequestDto joinRequestDto) {
        return feignClientService.saveUser(joinRequestDto);
    }

    public LoginResponseDto attemptAuthentication(LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = feignClientService.verifyUserCredentials(loginRequestDto); // valid한 user가 아닐 때의 예외는 user서비스에서 처리
        return successfulAuthentication(loginResponseDto);
    }

    public LoginResponseDto successfulAuthentication(LoginResponseDto loginResponseDto) {
        String accessToken = jwtUtil.createAccessToken(loginResponseDto.id(), loginResponseDto.role());
        String refreshToken = jwtUtil.createRefreshToken(loginResponseDto.id(), loginResponseDto.role());
        // TODO: Refresh Token Redis에 저장

        return AuthMapper.addToken(loginResponseDto, accessToken, refreshToken);
    }

    /**
     * Access Token 추출 후 검증하는 메서드
     *
     * @param headers
     * @return
     */
    public AuthResponseDto verify(HttpHeaders headers) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(headers);
        if (accessToken == null) {
            log.error("헤더에 토큰이 존재하지 않습니다.");
            throw new CustomException(AuthErrorCode.JWT_NOT_FOUND);
        } else {
            jwtUtil.isValidateToken(accessToken);
        }
        String userIdFromAccessToken = jwtUtil.getUserIdFromAccessToken(accessToken);
        String userRoleFromAccessToken = jwtUtil.getUserRoleFromAccessToken(accessToken);

        return AuthMapper.toGateway(userIdFromAccessToken, userRoleFromAccessToken);
    }
}
