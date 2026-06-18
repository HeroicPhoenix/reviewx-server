package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限码鉴权拦截器。
 *
 * 该拦截器只处理带有 @RequirePermission 的 Controller 方法或类。
 * 权限码来自登录用户上下文，由 AccessService 从数据库加载。
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    /**
     * 在 Controller 方法执行前检查权限。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequirePermission.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequirePermission.class);
        }
        if (annotation == null) {
            return true;
        }

        LoginUser loginUser = LoginUserContext.require();
        if (!loginUser.getPermissionCodes().contains(annotation.value())) {
            throw new BusinessException(403, "无权限访问该接口");
        }
        return true;
    }
}
