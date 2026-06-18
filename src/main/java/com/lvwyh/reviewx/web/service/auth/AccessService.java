package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.security.LoginUser;

public interface AccessService {

    LoginUser loadLoginUser(Long userId);

    Integer getTokenVersionOrInit(Long userId);

    int bumpTokenVersion(Long userId);
}
