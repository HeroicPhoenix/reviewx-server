package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.AdminProperties;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import com.lvwyh.reviewx.web.service.auth.LoginAttemptService;
import com.lvwyh.reviewx.web.service.auth.TokenService;
import com.lvwyh.reviewx.web.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 登录安全回归：不存在的用户与管理员登录开关都不能泄露凭证信息。 */
class AuthServiceImplLoginSecurityTest {

    @Test
    void disabledAdminLoginMustLookLikeInvalidCredentialsAfterNormalHashCheck() {
        SysUserMapper userMapper = mock(SysUserMapper.class);
        AccessService accessService = mock(AccessService.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtProperties jwtProperties = mock(JwtProperties.class);
        LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);
        AdminProperties adminProperties = new AdminProperties();
        adminProperties.setLoginEnabled(false);
        when(passwordEncoder.encode(anyString())).thenReturn("dummy-password-hash");
        AuthServiceImpl service = new AuthServiceImpl(userMapper, accessService, tokenService, passwordEncoder,
                jwtProperties, loginAttemptService, adminProperties);

        SysUser admin = new SysUser();
        admin.setUserId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("encoded-password");
        admin.setUserStatus(1);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        loginUser.setRoleCodes(Collections.singleton("ADMIN"));
        when(userMapper.selectByUsername("admin")).thenReturn(admin);
        when(accessService.loadLoginUser(1L)).thenReturn(loginUser);
        when(passwordEncoder.matches("123456", "encoded-password")).thenReturn(true);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.login("admin", "123456", "203.0.113.10"));

        assertEquals(401, error.getCode());
        assertEquals("用户名或密码错误", error.getMessage());
        verify(passwordEncoder).matches("123456", "encoded-password");
        verify(loginAttemptService).recordFailure("admin", "203.0.113.10");
    }

    @Test
    void nonexistentUserMustStillRunPasswordHashCheck() {
        SysUserMapper userMapper = mock(SysUserMapper.class);
        AccessService accessService = mock(AccessService.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtProperties jwtProperties = mock(JwtProperties.class);
        LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);
        AdminProperties adminProperties = new AdminProperties();
        when(passwordEncoder.encode(anyString())).thenReturn("dummy-password-hash");
        AuthServiceImpl service = new AuthServiceImpl(userMapper, accessService, tokenService, passwordEncoder,
                jwtProperties, loginAttemptService, adminProperties);
        when(userMapper.selectByUsername("missing-user")).thenReturn(null);
        when(passwordEncoder.matches("guess", "dummy-password-hash")).thenReturn(false);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.login("missing-user", "guess", "203.0.113.11"));

        assertEquals(401, error.getCode());
        assertEquals("用户名或密码错误", error.getMessage());
        verify(passwordEncoder).matches("guess", "dummy-password-hash");
        verify(accessService, never()).loadLoginUser(anyLong());
    }

    @Test
    void enabledAdminLoginMustAllowAnySourceWhenPasswordIsCorrect() {
        SysUserMapper userMapper = mock(SysUserMapper.class);
        AccessService accessService = mock(AccessService.class);
        TokenService tokenService = mock(TokenService.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtProperties jwtProperties = new JwtProperties();
        LoginAttemptService loginAttemptService = mock(LoginAttemptService.class);
        AdminProperties adminProperties = new AdminProperties();
        when(passwordEncoder.encode(anyString())).thenReturn("dummy-password-hash");
        AuthServiceImpl service = new AuthServiceImpl(userMapper, accessService, tokenService, passwordEncoder,
                jwtProperties, loginAttemptService, adminProperties);

        SysUser admin = new SysUser();
        admin.setUserId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("encoded-password");
        admin.setUserStatus(1);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        loginUser.setTokenVersion(1);
        loginUser.setRoleCodes(Collections.singleton("ADMIN"));
        when(userMapper.selectByUsername("admin")).thenReturn(admin);
        when(accessService.loadLoginUser(1L)).thenReturn(loginUser);
        when(passwordEncoder.matches("correct-password", "encoded-password")).thenReturn(true);
        when(tokenService.generate(1L, "admin", 1)).thenReturn("access-token");

        assertEquals("access-token",
                service.login("admin", "correct-password", "203.0.113.10").getAccessToken());
        verify(loginAttemptService).recordSuccess("admin");
    }
}
