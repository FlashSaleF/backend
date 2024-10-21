package com.flash.gateway.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory // RedisTemplate과 같이 Redis와의 연결정보가 구성돼야 함
    ) {
        // Access Token의 화이트리스트 캐시의 TTL을 9분으로 설정
        RedisCacheConfiguration accessTokenWhiteList = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(9))
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        // Access Token의 블랙리스트 캐시의 TTL을 10분으로 설정
        RedisCacheConfiguration accessTokenBlackList = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(10)) // TTL 10분
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        // Refresh Token의 화이트리스트 캐시의 TTL을 7일로 설정
        RedisCacheConfiguration refreshTokenWhiteList = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofDays(7)) // TTL 7일
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .withCacheConfiguration("accessTokenWhiteList", accessTokenWhiteList)
                .withCacheConfiguration("accessTokenBlackList", accessTokenBlackList)
                .withCacheConfiguration("refreshTokenWhiteList", refreshTokenWhiteList)
                .build();
    }
}
