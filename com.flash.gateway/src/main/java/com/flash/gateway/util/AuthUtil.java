package com.flash.gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flash.base.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Base64;

@Slf4j(topic = "Cache Util")
@Component
public class AuthUtil {

    public static String getAccessTokenFromHeader(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    public static String decodeJwtPayload(String token) {
        String[] parts = token.split("\\."); // JWT는 '.'으로 나뉨
        String payload = parts[1]; // 두 번째 부분이 페이로드
        return new String(Base64.getDecoder().decode(payload));
    }

    public static UserInfo extractUserInfoFromPayload(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode payloadNode = objectMapper.readTree(payload);

            String id = payloadNode.has("userId") ? payloadNode.get("userId").asText() : null;
            String role = payloadNode.has("auth") ? payloadNode.get("auth").asText() : null;

            if (id == null || role == null) {
                // 필요한 필드가 없는 경우에 대한 처리
                log.error("Invalid payload");
                // TODO: 커스텀 예외 던지기
//                throw new InvalidPayloadException("Invalid payload: missing required fields.");
            }

            return UserInfo.builder().id(id).role(role).build();

        } catch (JsonProcessingException e) {
            // TODO: 커스텀 예외?
            log.error("Failed to parse payload");
            throw new RuntimeException(e);
//            throw new InvalidPayloadException("Failed to parse JWT payload", e);
        }
    }

    public static ServerHttpRequest addHeader(ServerWebExchange exchange, UserInfo userInfo) {
        return exchange.getRequest().mutate()
                .header("X-User-ID", userInfo.id())
                .header("X-User-Role", userInfo.role())
                .build();
    }

}
