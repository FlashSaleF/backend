package com.flash.order.infrastructure.configuration;

import com.siot.IamportRestClient.IamportClient;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {
    @Value("${iamport.apiKey}")
    String apiKey;

    @Value("${iamport.apiSecret}")
    String apiSecret;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, apiSecret);
    }

    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create();
    }
}
