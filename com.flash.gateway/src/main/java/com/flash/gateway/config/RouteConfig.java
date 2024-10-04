package com.flash.gateway.config;

import com.flash.gateway.filter.TokenValidationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, TokenValidationFilter authFilter) {

        return builder.routes()
                .route("auth", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH")
                )
                .route("user", r -> r.path("/api/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://USER")
                )
                .build();
    }
}
