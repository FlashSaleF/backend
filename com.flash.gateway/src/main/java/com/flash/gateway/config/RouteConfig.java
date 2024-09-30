package com.flash.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("auth", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH")
                )
                .route("user", r -> r.path("/api/users/**")
                        .uri("lb://USER")
                )
                .build();
    }
}
