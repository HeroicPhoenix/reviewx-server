package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.service.auth.model.TokenClaims;

public interface TokenService {

    String generate(Long userId, String username, Integer tokenVersion);

    TokenClaims parse(String token);
}
