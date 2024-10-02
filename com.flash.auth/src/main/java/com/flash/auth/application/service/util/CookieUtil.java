package com.flash.auth.application.service.util;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.flash.auth.application.service.util.JwtUtil.REFRESH_TOKEN_TIME;

@Slf4j(topic = "Cookie Utility")
@Component
public class CookieUtil {

    // Refrsh Token을 담는 Cookie 이름
    public static final String COOKIE_NAME = "RefreshTokenCookie";

    /**
     * Refresh Token 생성 후 Cookie에 담기
     */
    public Cookie createCookieWithRefreshToken(String refreshToken) {
        // Cookie Value에는 공백이 불가능하므로 encoding 진행
        try {
            refreshToken = URLEncoder.encode(refreshToken, "utf-8").replaceAll("\\+", "%20");
            Cookie refreshTokenCookie = new Cookie(COOKIE_NAME, refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (REFRESH_TOKEN_TIME / 1000)); // 밀리 초가 아닌 초 단위기 때문에 /1000
            return refreshTokenCookie;
        } catch (UnsupportedEncodingException e) {
            log.error("Refresh Token encoding 오류: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
