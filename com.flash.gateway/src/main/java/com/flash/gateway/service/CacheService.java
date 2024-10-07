package com.flash.gateway.service;

import com.flash.gateway.util.dto.UserInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "userInfo", key = "#userId", unless = "#result == null")
    public UserInfo getUserById(String userId) {
        // 캐시 미스 시 null 반환
        return null;
    }

    @CachePut(value = "userInfo", key = "#userId")
    public UserInfo saveUserInfo(String userId, UserInfo userInfo) {
        return userInfo;
    }
}
