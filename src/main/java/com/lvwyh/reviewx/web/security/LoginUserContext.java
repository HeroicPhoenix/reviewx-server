package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.common.exception.BusinessException;

public final class LoginUserContext {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<LoginUser>();

    private LoginUserContext() {
    }

    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    public static LoginUser get() {
        return CONTEXT.get();
    }

    public static LoginUser require() {
        LoginUser user = get();
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return user;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
