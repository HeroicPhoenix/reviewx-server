package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.vo.auth.LoginVO;
import com.lvwyh.reviewx.web.vo.auth.MeVO;

public interface AuthService {

    LoginVO login(String username, String password);

    MeVO me(Long userId);

    void logout(Long userId);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
