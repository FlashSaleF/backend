package com.flash.auth.application.service;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.request.LoginRequestDto;
import com.flash.auth.application.dto.response.AuthResponseDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import com.flash.auth.application.service.util.AuthMapper;
import com.flash.auth.application.service.util.CacheUtil;
import com.flash.auth.application.service.util.CookieUtil;
import com.flash.auth.application.service.util.JwtUtil;
import com.flash.auth.domain.exception.AuthErrorCode;
import com.flash.base.dto.UserInfo;
import com.flash.base.exception.CustomException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final CacheUtil cacheUtil;
    private final CookieUtil cookieUtil;

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

        // 로그인 성공 시 Access Token, Refresh Token 화이트리스트로 등록
        cacheUtil.saveAccessToken(loginResponseDto.id(), AuthMapper.toUserInfo(loginResponseDto.id(), loginResponseDto.role(), accessToken));
        cacheUtil.saveRefreshToken(loginResponseDto.id(), AuthMapper.toUserInfo(loginResponseDto.id(), loginResponseDto.role(), refreshToken));

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
            log.error("헤더에 Access 토큰이 존재하지 않습니다.");
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        if (jwtUtil.isValidateAccessToken(accessToken)) {
            String userIdFromAccessToken = jwtUtil.getUserIdFromAccessToken(accessToken);
            String userRoleFromAccessToken = jwtUtil.getUserRoleFromAccessToken(accessToken);
            return AuthMapper.toGateway(userIdFromAccessToken, userRoleFromAccessToken);

        } else {
            AuthResponseDto authResponseDto = reIssue(headers);
            log.info("토큰이 재발급되었습니다.");
            return authResponseDto;
        }
    }

    public AuthResponseDto reIssue(HttpHeaders headers) {
        Cookie cookie = cookieUtil.extractRefreshTokenCookie(headers);
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(cookie);
        String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
        UserInfo fromValidRefreshToken = cacheUtil.getValidRefreshToken(userId);

        if (refreshToken == null || fromValidRefreshToken == null) {
            log.error("Refresh Token이 존재하지 않습니다.");
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
        } else {
            jwtUtil.isValidateRefreshToken(refreshToken);
        }

        String reIssuedAccessToken = jwtUtil.createAccessToken(userId, fromValidRefreshToken.role());
        String rotatedRefreshToken = jwtUtil.createRefreshToken(userId, fromValidRefreshToken.role());

        // 새로 발급 받은 Access 및 Refresh 토큰 화이트리스트 처리
        cacheUtil.saveAccessToken(userId, AuthMapper.toUserInfo(userId, fromValidRefreshToken.role(), reIssuedAccessToken));
        cacheUtil.saveRefreshToken(userId, AuthMapper.toUserInfo(userId, fromValidRefreshToken.role(), rotatedRefreshToken));

        return AuthMapper.toGateway(userId, fromValidRefreshToken.role());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtUtil.getAccessTokenFromHeader(request);
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null || cacheUtil.getValidRefreshToken(jwtUtil.getUserIdFromRefreshToken(refreshToken)) == null) {
            log.error("토큰이 존재하지 않거나 유효하지 않습니다.");
            throw new CustomException(AuthErrorCode.UNAUTHORIZED_JWT);
        }
        // 화이트리스트에서 삭제 및 블랙리스트 처리
        cacheUtil.deleteAccessToken(jwtUtil.getUserIdFromRefreshToken(refreshToken));
        cacheUtil.setAccessTokenToBlackList(accessToken, AuthMapper.toUserInfo(jwtUtil.getUserIdFromRefreshToken(refreshToken), jwtUtil.getUserRoleFromRefreshToken(refreshToken), accessToken));
        cacheUtil.deleteRefreshToken(jwtUtil.getUserIdFromRefreshToken(refreshToken));

        // Todo: 헤더 및 쿠키에서 삭제?
    }
}
