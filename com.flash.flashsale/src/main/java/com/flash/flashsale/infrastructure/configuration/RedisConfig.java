package com.flash.flashsale.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flash.flashsale.application.dto.response.FlashSaleProductListResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, FlashSaleProductListResponseDto> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, FlashSaleProductListResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // JSON 직렬화 사용
        Jackson2JsonRedisSerializer<FlashSaleProductListResponseDto> serializer = new Jackson2JsonRedisSerializer<>(FlashSaleProductListResponseDto.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        serializer.setObjectMapper(objectMapper);

        template.setDefaultSerializer(serializer);
        return template;
    }
}