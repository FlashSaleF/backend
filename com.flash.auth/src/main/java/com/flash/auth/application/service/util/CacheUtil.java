package com.flash.auth.application.service.util;

import com.flash.base.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j(topic = "Cache Util")
@Service
public class CacheUtil {

    // Todo: 직관성을 위해 파라미터 및 메서드 이름으로 구별했으나, 기능은 비슷하므로 코드를 합칠 수 있음.

    /**
     * Access Token 화이트리스트 처리
     *
     * @param userId
     * @param userInfo
     * @return
     */
    @CachePut(cacheNames = "accessTokenWhiteList", key = "#userId")
    public UserInfo saveAccessToken(String userId, UserInfo userInfo) {
        return userInfo;
    }

    /**
     * Access Token 블랙리스트 처리
     *
     * @param accessToken
     * @param userInfo
     * @return
     */
    @CachePut(cacheNames = "accessTokenBlackList", key = "#accessToken")
    public UserInfo setAccessTokenToBlackList(String accessToken, UserInfo userInfo) {
        return userInfo;
    }

    /**
     * Refresh Token 화이트리스트 처리
     *
     * @param userId
     * @param userInfo
     * @return
     */
    @CachePut(cacheNames = "refreshTokenWhiteList", key = "#userId")
    public UserInfo saveRefreshToken(String userId, UserInfo userInfo) {
        return userInfo;
    }

    /**
     * Access Token 조회
     *
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "accessTokenWhiteList", key = "#userId", unless = "#result == null")
    public UserInfo getValidAccessToken(String userId) {
        // 캐시 미스 시 null 반환
        return null;
    }


    /**
     * Refresh Token 조회
     *
     * @param userId
     * @return
     */
    @Cacheable(cacheNames = "refreshTokenWhiteList", key = "#userId", unless = "#result == null")
    public UserInfo getValidRefreshToken(String userId) {
        // 캐시 미스 시 null 반환
        return null;
    }

    /**
     * 화이트리스트 처리된 Access Token을 삭제
     */
    @CacheEvict(cacheNames = "accessTokenWhiteList", key = "#userId")
    public void deleteAccessToken(String userId) {
        log.info("로그아웃 되었습니다: {}", userId);
        // 캐시에서 해당 userId로 저장된 데이터를 삭제
    }

    /**
     * 화이트리스트 처리된 Refresh Token을 삭제
     */
    @CacheEvict(cacheNames = "refreshTokenWhiteList", key = "#userId")
    public void deleteRefreshToken(String userId) {
        // 삭제된 후 특별히 처리할 내용이 없으므로 빈 메서드로 구현
    }
}
