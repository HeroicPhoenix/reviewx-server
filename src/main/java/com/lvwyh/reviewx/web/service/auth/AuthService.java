package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.vo.auth.LoginVO;
import com.lvwyh.reviewx.web.vo.auth.MeVO;

/**
 * 认证服务接口。
 *
 * 提供登录、查询当前用户、登出和修改密码能力。
 */
public interface AuthService {

    /** 校验用户名密码并签发 JWT。 */
    LoginVO login(String username, String password);

    /** 查询当前登录用户信息。 */
    MeVO me(Long userId);

    /** 登出当前用户，使现有 Token 失效。 */
    void logout(Long userId);

    /** 修改当前用户密码并使旧 Token 失效。 */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
