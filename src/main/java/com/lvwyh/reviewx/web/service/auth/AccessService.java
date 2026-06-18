package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.security.LoginUser;

/**
 * 登录访问上下文服务。
 *
 * 负责加载当前用户的状态、角色、权限和 Token 版本。
 */
public interface AccessService {

    /** 按用户 ID 加载完整登录上下文。 */
    LoginUser loadLoginUser(Long userId);

    /** 获取用户 Token 版本；不存在时自动初始化。 */
    Integer getTokenVersionOrInit(Long userId);

    /** 提升用户 Token 版本，使旧 Token 失效。 */
    int bumpTokenVersion(Long userId);
}
