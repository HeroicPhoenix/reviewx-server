package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.common.exception.BusinessException;

/**
 * 登录用户线程上下文。
 *
 * 每个 HTTP 请求在线程内保存一份 LoginUser，Controller/Service 可从这里读取当前用户。
 * 请求结束时必须清理，避免 Web 容器线程复用造成用户串号。
 */
public final class LoginUserContext {

    /** 当前请求线程绑定的登录用户。 */
    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<LoginUser>();

    private LoginUserContext() {
    }

    /**
     * 写入当前请求的登录用户。
     */
    public static void set(LoginUser user) {
        CONTEXT.set(user);
    }

    /**
     * 获取当前登录用户；未登录时返回 null。
     */
    public static LoginUser get() {
        return CONTEXT.get();
    }

    /**
     * 强制获取当前登录用户。
     *
     * 用于必须登录的业务接口，如果上下文不存在则直接返回 401。
     */
    public static LoginUser require() {
        LoginUser user = get();
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        return user;
    }

    /**
     * 清理当前请求上下文。
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
