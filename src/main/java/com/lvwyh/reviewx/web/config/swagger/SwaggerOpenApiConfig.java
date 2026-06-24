package com.lvwyh.reviewx.web.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Swagger / OpenAPI 配置。
 *
 * 注册 Bearer Token 和 API Key 两种认证方案，让 Swagger UI 支持登录后携带令牌，
 * 也支持使用用户创建的 API Key 访问受保护接口。
 */
@Configuration
public class SwaggerOpenApiConfig {

    /** Swagger UI 里显示的 Bearer Token 安全方案名称。 */
    private static final String BEARER_SECURITY_SCHEME_NAME = "BearerAuth";

    /** Swagger UI 里显示的 API Key 安全方案名称。 */
    private static final String API_KEY_SECURITY_SCHEME_NAME = "ApiKeyAuth";

    /** 登录接口本身不需要携带 Token。 */
    private static final String LOGIN_PATH = "/api/auth/login";

    /**
     * OpenAPI 基础信息和全局认证方案。
     *
     * 在 Swagger UI 右上角会出现 Authorize 按钮，可输入登录接口返回的 accessToken，
     * 也可输入用户创建的 API Key。
     */
    @Bean
    public OpenAPI reviewXOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ReviewX API")
                        .description("ReviewX 刷题工具后端接口文档")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(API_KEY_SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name("X-API-Key")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }

    /**
     * 给需要登录的接口追加认证要求。
     *
     * 登录接口会清空 security，避免 Swagger UI 要求先授权才能调用登录。
     * OpenAPI 中多个 SecurityRequirement 对象表示“或”的关系，因此这里允许 Bearer Token 或 API Key 任一方式通过。
     */
    @Bean
    public OpenApiCustomiser bearerTokenOpenApiCustomiser() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            List<SecurityRequirement> securityRequirements = Arrays.asList(
                    new SecurityRequirement().addList(BEARER_SECURITY_SCHEME_NAME),
                    new SecurityRequirement().addList(API_KEY_SECURITY_SCHEME_NAME)
            );
            for (Map.Entry<String, PathItem> entry : openApi.getPaths().entrySet()) {
                String path = entry.getKey();
                PathItem pathItem = entry.getValue();
                if (LOGIN_PATH.equals(path)) {
                    applySecurity(pathItem, Collections.emptyList());
                    continue;
                }
                applySecurity(pathItem, securityRequirements);
            }
        };
    }

    /**
     * 将 security 配置应用到同一个 path 下的所有 HTTP 方法。
     */
    private void applySecurity(PathItem pathItem, List<SecurityRequirement> securityRequirements) {
        if (pathItem == null || pathItem.readOperations() == null) {
            return;
        }
        for (Operation operation : pathItem.readOperations()) {
            operation.setSecurity(securityRequirements);
        }
    }
}
