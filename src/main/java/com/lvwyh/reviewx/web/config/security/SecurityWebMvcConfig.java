package com.lvwyh.reviewx.web.config.security;

import com.lvwyh.reviewx.web.security.JwtAuthInterceptor;
import com.lvwyh.reviewx.web.security.PermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityWebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    private final PermissionInterceptor permissionInterceptor;

    public SecurityWebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor,
                                PermissionInterceptor permissionInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor).addPathPatterns("/**").order(100);
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").order(200);
    }
}
