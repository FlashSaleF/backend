package com.flash.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced  // 이 어노테이션이 WebClient에 로드 밸런서를 적용할 수 있도록 함
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
