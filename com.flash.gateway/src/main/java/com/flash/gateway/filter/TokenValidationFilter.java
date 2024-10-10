package com.flash.gateway.filter;

import com.flash.gateway.service.CacheService;
import com.flash.gateway.util.AuthUtil;
import com.flash.gateway.util.dto.AuthResponseDto;
import com.flash.gateway.util.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j(topic = "Token Validation Filter")
@Component
@RequiredArgsConstructor
public class TokenValidationFilter implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;
    private final CacheService cacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        if (path.contains("/swagger-ui/") || path.contains("/v3/api-docs") || path.contains("/swagger-resources")) {
            return chain.filter(exchange);  // 필터 통과
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String bearerAccessToken = headers.getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerAccessToken == null) {
            // TODO: 커스텀 예외
            return Mono.error(new RuntimeException("Authorization header is missing"));
        }

        String accessToken = AuthUtil.getAccessTokenFromHeader(bearerAccessToken);
        String jwtPayload = AuthUtil.decodeJwtPayload(accessToken);
        UserInfo userInfoFromPayload = AuthUtil.extractUserInfoFromPayload(jwtPayload);

        UserInfo cachedUserInfo = cacheService.getUserById(userInfoFromPayload.id());
        if (cachedUserInfo != null && isCacheValid(cachedUserInfo, bearerAccessToken, userInfoFromPayload)) {
            log.info("Cache Hit: UserId {}", cachedUserInfo.id());
            ServerHttpRequest request = AuthUtil.addHeader(exchange, cachedUserInfo);
            return chain.filter(exchange.mutate().request(request).build());
        } else {
            log.info("Cache Miss: UserId {}", userInfoFromPayload.id());
            return sendAuthRequestAndCache(exchange, chain, bearerAccessToken, headers);
        }
    }

    private boolean isCacheValid(UserInfo cachedUserInfo, String bearerAccessToken, UserInfo userInfoFromPayload) {
        return cachedUserInfo.accessToken().equals(bearerAccessToken) &&
                cachedUserInfo.role().equals(userInfoFromPayload.role()) &&
                cachedUserInfo.id().equals(userInfoFromPayload.id());
    }

    private Mono<Void> sendAuthRequestAndCache(ServerWebExchange exchange, GatewayFilterChain chain,
                                               String bearerAccessToken, HttpHeaders headers) {
        return webClientBuilder.build()
                .post()
                .uri("lb://AUTH/api/auth/verify")
                .headers(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION));
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE));
                    httpHeaders.set(HttpHeaders.ACCEPT, headers.getFirst(HttpHeaders.ACCEPT));
                })
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .timeout(Duration.ofSeconds(2))
                .flatMap(authResponseDto -> {
                    UserInfo user = UserInfo.builder()
                            .id(authResponseDto.id())
                            .role(authResponseDto.role())
                            .accessToken(bearerAccessToken)
                            .build();

                    cacheService.saveUserInfo(authResponseDto.id(), user);
                    ServerHttpRequest request = AuthUtil.addHeader(exchange, user);
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .doOnError(error -> log.error("Error during token validation: {}", error.getMessage()))
                .then();
    }
}