package com.flash.auth.application.service.util;

import com.flash.auth.domain.exception.AuthErrorCode;
import com.flash.base.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 발급과 검증을 담당하는 클래스
 */
@Slf4j(topic = "JWT Utility")
@Component
public class JwtUtil {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 id 값의 KEY
    public static final String AUTHENTICATION_KEY = "userId";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // Refresh Token 만료기간
    public static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일
    // Access Token 만료기간
    private static final long ACCESS_TOKEN_TIME = 10 * 60 * 1000L; // 10분
    private SecretKey secretKey;
    private SecretKey refreshSecretKey;

    /**
     * .env파일에 있는 키를 암호화해서 SecretKey객체 타입으로 저장
     * 양방향 암호화의 대칭키 방식 사용: 동일한 방식으로 암호화, 복호화 진행. cf. 비대칭키 방식으로도 가능
     */
    public JwtUtil(@Value("${jwt.secret.key}") String secret, @Value("${jwt.refresh.secret.key}") String refresh) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(String userId, String role) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .claim(AUTHENTICATION_KEY, userId)
                        .claim(AUTHORIZATION_KEY, role)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_TIME))
                        .signWith(secretKey)
                        .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String userId, String role) {
        return Jwts.builder()
                .claim(AUTHENTICATION_KEY, userId)
                .claim(AUTHORIZATION_KEY, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME))
                .signWith(refreshSecretKey)
                .compact();
    }

    /**
     * Header에서 Access Token 추출
     */
    public String getAccessTokenFromHeader(HttpHeaders headers) {
        String bearerToken = headers.getFirst(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7).trim(); // 순순한 토큰을 추출하기 위해 substring(prefix 자름)
        }
        return null;
    }

    /**
     * Cookie에서 Refresh Token 추출
     */
    public String getRefreshTokenFromCookie(Cookie cookie) {
        String refresh = null;
        if (cookie.getName().equals(CookieUtil.COOKIE_NAME)) {
            refresh = cookie.getValue();
        }
        return refresh;
    }

    // Header에서 Access Token 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7).trim(); // 순수한 토큰 가져오기 위해 substring(BEARER 자름)
        }
        return null;
    }

    // Cookie에서 Refresh Token 가져오기
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CookieUtil.COOKIE_NAME)) {
                refresh = cookie.getValue();
            }
        }
        return refresh;
    }

    /**
     * 토큰 검증하는 메서드
     *
     * @param token
     * @return
     */
    public boolean isValidateAccessToken(String accessToken) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 만료된 Access 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Access: Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(AuthErrorCode.UNSUPPORTED_JWT);
        } catch (MalformedJwtException | SecurityException e) {
            log.error("Access: Invalid JWT token, 유효하지 않은 JWT token 입니다.");
            throw new CustomException(AuthErrorCode.INVALID_JWT);
        } catch (IllegalArgumentException e) {
            log.error("Access: JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new CustomException(AuthErrorCode.EMPTY_JWT);
        }
    }

    public boolean isValidateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 만료된 Refresh 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Refresh: Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
            throw new CustomException(AuthErrorCode.UNSUPPORTED_JWT);
        } catch (MalformedJwtException | SecurityException e) {
            log.error("Refresh: Invalid JWT token, 유효하지 않은 JWT token 입니다.");
            throw new CustomException(AuthErrorCode.INVALID_JWT);
        } catch (IllegalArgumentException e) {
            log.error("Refresh: JWT claims is empty, 잘못된 JWT 토큰 입니다.");
            throw new CustomException(AuthErrorCode.EMPTY_JWT);
        }
    }

    // Access 토큰에서 사용자 id 가져오기
    public String getUserIdFromAccessToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(AUTHENTICATION_KEY, String.class);
    }

    // Access 토큰에서 사용자 role 가져오기
    public String getUserRoleFromAccessToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(AUTHORIZATION_KEY, String.class);
    }

    // Refresh 토큰에서 사용자 정보 가져오기
    public String getUserIdFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get(AUTHENTICATION_KEY, String.class);
    }

    public String getUserRoleFromRefreshToken(String token) {
        return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().get(AUTHORIZATION_KEY, String.class);
    }

}
