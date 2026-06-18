package com.lvwyh.reviewx.web.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限码注解。
 *
 * 标注在 Controller 方法或类上后，PermissionInterceptor 会校验当前登录用户是否拥有该权限码。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequirePermission {

    /** 需要具备的权限编码，例如 question:view。 */
    String value();
}
