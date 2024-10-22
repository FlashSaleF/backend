package com.flash.auth.infrastructure.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Document", description = "auth", version = "v3")
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .security(Collections.singletonList(securityRequirement))
                .addServersItem(new Server().url("/"))
                // /api/join, /api/login에 대한 보안 요구사항을 제거
                .paths(filterSecurityForPublicPaths());
    }

    // 특정 경로에 대해 보안 요구사항을 제거하는 메서드
    private Paths filterSecurityForPublicPaths() {
        Paths paths = new Paths();
        // JWT 인증이 필요하지 않은 경로 설정
        paths.addPathItem("/api/join", new PathItem().get(new Operation().security(Collections.emptyList())));
        paths.addPathItem("/api/login", new PathItem().get(new Operation().security(Collections.emptyList())));
        // 다른 경로들은 기본 보안 적용
        return paths;
    }
}

