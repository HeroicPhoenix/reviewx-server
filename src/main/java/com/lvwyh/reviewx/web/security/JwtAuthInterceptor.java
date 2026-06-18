package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import com.lvwyh.reviewx.web.service.auth.TokenService;
import com.lvwyh.reviewx.web.service.auth.model.TokenClaims;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/login",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/druid/**",
            "/actuator/health"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final TokenService tokenService;

    private final AccessService accessService;

    public JwtAuthInterceptor(TokenService tokenService, AccessService accessService) {
        this.tokenService = tokenService;
        this.accessService = accessService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (isWhiteListUri(request.getRequestURI())) {
            return true;
        }

        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(401, "缺少访问令牌");
        }

        TokenClaims claims = tokenService.parse(authorization.substring(BEARER_PREFIX.length()));
        if (claims.getUserId() == null || claims.getTokenVersion() == null) {
            throw new BusinessException(401, "访问令牌无效");
        }

        LoginUser loginUser = accessService.loadLoginUser(claims.getUserId());
        if (!claims.getTokenVersion().equals(loginUser.getTokenVersion())) {
            throw new BusinessException(401, "访问令牌已失效，请重新登录");
        }
        LoginUserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginUserContext.clear();
    }

    private boolean isWhiteListUri(String requestUri) {
        for (String whitePath : WHITE_LIST) {
            if (pathMatcher.match(whitePath, requestUri)) {
                return true;
            }
        }
        return false;
    }
}
