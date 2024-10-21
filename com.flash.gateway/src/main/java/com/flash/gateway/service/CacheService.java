package com.flash.gateway.service;

import com.flash.base.dto.UserInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(cacheNames = "accessTokenWhiteList", key = "#userId", unless = "#result == null")
    public UserInfo getAccessToken(String userId) {
        // 캐시 미스 시 null 반환
        return null;
    }

    @Cacheable(cacheNames = "accessTokenBlackList", key = "#accessToken", unless = "#result == null")
    public UserInfo isBlackAccessToken(String accessToken) {
        // 캐시 미스 시 null 반환
        return null;
    }


}
