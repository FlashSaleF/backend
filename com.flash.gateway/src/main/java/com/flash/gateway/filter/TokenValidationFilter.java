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

    // TODO: 로그 지우기, Mono, Flux 동작 방식 파악. switchIfEmpty()문 사용했을 때 empty()가 넘어오는 이유 파악

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청에서 Authorization 헤더 가져오기
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String bearerAccessToken = headers.getFirst(HttpHeaders.AUTHORIZATION);

        // 토큰이 없는 경우, 인증을 바로 실패 처리
        if (bearerAccessToken == null) {
            // TODO: 커스텀 예외로 변경
            return Mono.error(new RuntimeException("Authorization header is missing"));
        }
        // 헤더에서 Access Token을 추출하여 클레임의 값 decoding.
        String accessTokenFromHeader = AuthUtil.getAccessTokenFromHeader(bearerAccessToken);
        String jwtPayload = AuthUtil.decodeJwtPayload(accessTokenFromHeader);
        UserInfo userInfoFromPayload = AuthUtil.extractUserInfoFromPayload(jwtPayload);

        return Mono.justOrEmpty(cacheService.getUserById(userInfoFromPayload.id()))
                // 캐시된 데이터가 존재하고, 토큰과 role의 값이 같으면 라우팅 처리
                .flatMap(cachedUserInfo -> {
                    if (cachedUserInfo == null) {
                        log.info("Cache miss: No cached user info found for userId {}", userInfoFromPayload.id());
                        return sendAuthRequestAndCache(exchange, chain, bearerAccessToken, headers);
                    }
                    boolean accessTokenMatches = cachedUserInfo.accessToken().equals(bearerAccessToken);
                    boolean roleMatches = cachedUserInfo.role().equals(userInfoFromPayload.role());
                    boolean userIdMatches = cachedUserInfo.id().equals(userInfoFromPayload.id());

                    if (accessTokenMatches && roleMatches && userIdMatches) {
                        log.info("Cache hit: Cached user info matches for userId {}", cachedUserInfo.id());
                        ServerHttpRequest request = AuthUtil.addHeader(exchange, cachedUserInfo);
                        return chain.filter(exchange.mutate().request(request).build())
                                .doOnSuccess(success -> log.info("Request successfully routed for userId {}", cachedUserInfo.id()))
                                .then();  // Void 타입으로 반환하도록 수정
                    } else {
                        log.info("Cache miss: Cache info does not match for userId {}. AccessTokenMatches: {}, RoleMatches: {}, UserIdMatches: {}",
                                cachedUserInfo.id(), accessTokenMatches, roleMatches, userIdMatches);
                        return sendAuthRequestAndCache(exchange, chain, bearerAccessToken, headers);
                    }
                });
    }

    private Mono<Void> sendAuthRequestAndCache(ServerWebExchange exchange, GatewayFilterChain chain,
                                               String bearerAccessToken, HttpHeaders headers) {
        return webClientBuilder.build()
                .post()
                .uri("lb://AUTH/api/auth/verify")
                // TODO: 기존에는 headers를 그대로 전달했는데 문제가 발생했었음. 원인 파악 확실히!!!
                // 조회 시에는 문제가 없었으나, get요청 이외의 요청에서는 1번 200ok되면 2번째는 에러가 발생했음
                .headers(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION));
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, headers.getFirst(HttpHeaders.CONTENT_TYPE));
                    httpHeaders.set(HttpHeaders.ACCEPT, headers.getFirst(HttpHeaders.ACCEPT));
                })
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .timeout(Duration.ofSeconds(2))
                .flatMap(authResponseDto -> {
                    log.info("userId: {}", authResponseDto.id());
                    log.info("role: {}", authResponseDto.role());

                    UserInfo user = UserInfo.builder()
                            .id(authResponseDto.id())
                            .role(authResponseDto.role())
                            .accessToken(bearerAccessToken)
                            .build();

                    // 캐시에 저장
                    cacheService.saveUserInfo(authResponseDto.id(), user);
                    // 헤더에 추가
                    ServerHttpRequest request = AuthUtil.addHeader(exchange, user);
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .then()  // Void 타입으로 반환하도록 수정
                .doOnError(error -> log.error("Error during token validation: {}", error.getMessage()));
    }
}
