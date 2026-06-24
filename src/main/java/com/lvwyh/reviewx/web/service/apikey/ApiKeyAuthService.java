package com.lvwyh.reviewx.web.service.apikey;

import com.lvwyh.reviewx.web.security.LoginUser;

/**
 * API Key 认证服务。
 */
public interface ApiKeyAuthService {

    /**
     * 校验 API Key 并加载对应登录用户上下文。
     */
    LoginUser authenticate(String apiKey);
}
