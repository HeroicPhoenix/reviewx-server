package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.security.AdminNetworkAccessService;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import com.lvwyh.reviewx.web.service.auth.LoginAttemptService;
import com.lvwyh.reviewx.web.service.auth.TokenService;
import com.lvwyh.reviewx.web.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 登录安全回归：非白名单管理员不能通过响应判断密码是否正确。 */
class AuthServiceImplLoginSecurityTest {

    @Test
    void rejectedAdminLoginMustLookLikeInvalidCredentialsWithoutCheckingPassword() {
        SysUserMapper userMapper = mock(SysUserMapper.class);
        AccessService accessService = mock(AccessService.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtProperties jwtProperties = mock(JwtProperties.class);
        LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);
        AdminNetworkAccessService adminNetworkAccessService = mock(AdminNetworkAccessService.class);
        AuthServiceImpl service = new AuthServiceImpl(userMapper, accessService, tokenService, passwordEncoder,
                jwtProperties, loginAttemptService, adminNetworkAccessService);

        SysUser admin = new SysUser();
        admin.setUserId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("encoded-password");
        admin.setUserStatus(1);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        when(userMapper.selectByUsername("admin")).thenReturn(admin);
        when(accessService.loadLoginUser(1L)).thenReturn(loginUser);
        doThrow(new BusinessException(403, "超级管理员仅允许从指定的局域网电脑访问"))
                .when(adminNetworkAccessService).checkAllowed(loginUser, "203.0.113.10");

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.login("admin", "123456", "203.0.113.10"));

        assertEquals(401, error.getCode());
        assertEquals("用户名或密码错误", error.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(loginAttemptService).recordAddressFailure("203.0.113.10");
        verify(loginAttemptService, never()).recordFailure("admin", "203.0.113.10");
    }
}
