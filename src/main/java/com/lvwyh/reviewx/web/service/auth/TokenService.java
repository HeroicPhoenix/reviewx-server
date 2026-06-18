package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.service.auth.model.TokenClaims;

/**
 * Token 服务接口。
 *
 * 隔离 JWT 具体实现，业务层只依赖生成和解析能力。
 */
public interface TokenService {

    /** 生成访问令牌。 */
    String generate(Long userId, String username, Integer tokenVersion);

    /** 解析并校验访问令牌。 */
    TokenClaims parse(String token);
}
