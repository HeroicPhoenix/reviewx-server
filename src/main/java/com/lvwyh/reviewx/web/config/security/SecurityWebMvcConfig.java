package com.lvwyh.reviewx.web.config.security;

import com.lvwyh.reviewx.web.security.JwtAuthInterceptor;
import com.lvwyh.reviewx.web.security.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 安全拦截器注册配置。
 *
 * 认证拦截器先执行，负责解析 JWT；权限拦截器后执行，负责校验权限码。
 */
@Configuration
public class SecurityWebMvcConfig implements WebMvcConfigurer {

    /** JWT 认证拦截器。 */
    private final JwtAuthInterceptor jwtAuthInterceptor;

    /** 权限码鉴权拦截器。 */
    private final PermissionInterceptor permissionInterceptor;

    public SecurityWebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor,
                                PermissionInterceptor permissionInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.permissionInterceptor = permissionInterceptor;
    }

    /**
     * 注册全局拦截器。
     *
     * order 值越小越先执行，因此认证必须排在鉴权之前。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor).addPathPatterns("/**").order(100);
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").order(200);
    }
}
