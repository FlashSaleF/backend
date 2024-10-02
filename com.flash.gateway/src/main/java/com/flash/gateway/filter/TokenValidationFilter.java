package com.flash.gateway.filter;

import com.flash.gateway.util.AuthResponseDto;
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

@Slf4j(topic = "Token Validation Filter")
@Component
@RequiredArgsConstructor
public class TokenValidationFilter implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청에서 Authorization 헤더 가져오기
        HttpHeaders headers = exchange.getRequest().getHeaders();

        // 토큰이 없는 경우, 인증을 바로 실패 처리
        if (headers.getFirst(HttpHeaders.AUTHORIZATION) == null) {
            // TODO: 커스텀 예외로 변경
            return Mono.error(new RuntimeException("Authorization header is missing"));
        }
        // TODO: 헤더에서 Access Token을 추출하여 클레임의 값 decoding.

        // TODO: 위에서 뽑은 값을 키로 캐싱된 클레임이 있는지 조회. 키 값을 뭐로 할지만 잘 정하면 될 듯!

        // TODO: 키-값이 맞으면, return(클라이언트 요청의 엔드포인트로 라우팅)

        // 맞지 않으면, WebClient로 Auth 서버로 인증 요청 보내기
        return webClientBuilder.build()
                .post()
                .uri("lb://AUTH/api/auth/verify") // Auth 서버에 토큰 검증 요청
                .headers(httpHeaders -> httpHeaders.addAll(headers)) // 기존 헤더 전달
                .retrieve()
                .bodyToMono(AuthResponseDto.class)
                .flatMap(userInfo -> {
                    log.info("userId: " + userInfo.id());
                    log.info("role: " + userInfo.role());

                    // TODO: 받아온 데이터 캐싱 처리


                    // AuthResponseDto에서 id와 role을 추출하고 헤더에 추가
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-ID", userInfo.id())
                            .header("X-User-Role", userInfo.role())
                            .build();
                    // 헤더에 userId와 role을 추가한 새로운 요청으로 필터 체인 계속 진행
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .doOnError(error -> log.error("Error during token validation: {}", error.getMessage()));
    }
}
