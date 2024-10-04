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
                .route("auth", r -> r.path("/api/auth", "/api/auth/**")
                        .uri("lb://AUTH")
                )
                .route("user", r -> r.path("/api/users", "/api/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://USER")
                )
                .route("vendor", r -> r.path("/api/vendors", "/api/vendors/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://VENDOR")
                )
                .route("order", r -> r.path("/api/orders", "/api/orders/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://ORDER")
                )
                .route("flash-sale", r -> r.path("/api/flash-sales", "/api/flash-sales/**", "/api/flash-sale-products", "/api/flash-sale-products/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://FLASH-SALE")
                )
                .build();
    }
}
