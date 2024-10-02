package com.flash.auth.application.service.util;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

}
