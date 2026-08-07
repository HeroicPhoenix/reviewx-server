package com.lvwyh.reviewx.web.service.auth.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.AdminProperties;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import com.lvwyh.reviewx.web.service.auth.AuthService;
import com.lvwyh.reviewx.web.service.auth.TokenService;
import com.lvwyh.reviewx.web.service.auth.LoginAttemptService;
import com.lvwyh.reviewx.web.vo.auth.LoginVO;
import com.lvwyh.reviewx.web.vo.auth.MeVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 认证服务实现。
 *
 * 负责用户名密码登录、当前用户信息组装、登出和修改密码。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "用户名或密码错误";
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    private final SysUserMapper sysUserMapper;
    private final AccessService accessService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    private final LoginAttemptService loginAttemptService;
    private final AdminProperties adminProperties;
    private final String dummyPasswordHash;

    public AuthServiceImpl(SysUserMapper sysUserMapper,
                           AccessService accessService,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder,
                           JwtProperties jwtProperties,
                           LoginAttemptService loginAttemptService,
                           AdminProperties adminProperties) {
        this.sysUserMapper = sysUserMapper;
        this.accessService = accessService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
        this.loginAttemptService = loginAttemptService;
        this.adminProperties = adminProperties;
        // 不存在或禁用的用户也执行一次与真实账号相同的密码哈希校验，降低用户名时序枚举风险。
        this.dummyPasswordHash = passwordEncoder.encode("reviewx-dummy-password-for-timing-only");
    }

    /**
     * 登录入口。
     *
     * 校验用户名、用户状态和密码，全部通过后签发 JWT。
     */
    @Override
    public LoginVO login(String username, String password, String clientAddress) {
        loginAttemptService.checkAllowed(username, clientAddress);
        SysUser user = sysUserMapper.selectByUsername(username);
        boolean activeUser = user != null && Integer.valueOf(1).equals(user.getUserStatus());
        String passwordHash = activeUser ? user.getPasswordHash() : dummyPasswordHash;
        boolean passwordMatches = passwordEncoder.matches(password, passwordHash);
        // 已存在账号无论密码是否正确都加载相同的角色信息，避免管理员开关场景泄露密码是否正确。
        LoginUser loginUser = activeUser ? accessService.loadLoginUser(user.getUserId()) : null;
        boolean adminLoginDisabled = loginUser != null
                && loginUser.getRoleCodes().contains(ADMIN_ROLE_CODE)
                && !adminProperties.isLoginEnabled();
        if (!activeUser || !passwordMatches || adminLoginDisabled) {
            loginAttemptService.recordFailure(username, clientAddress);
            throw new BusinessException(401, INVALID_CREDENTIALS_MESSAGE);
        }
        loginAttemptService.recordSuccess(username);

        LoginVO vo = new LoginVO();
        vo.setAccessToken(tokenService.generate(
                user.getUserId(), user.getUsername(), loginUser.getTokenVersion()));
        vo.setTokenType("Bearer");
        vo.setExpireIn(jwtProperties.getExpireSeconds());
        return vo;
    }

    /**
     * 查询当前登录用户信息。
     */
    @Override
    public MeVO me(Long userId) {
        LoginUser loginUser = accessService.loadLoginUser(userId);
        List<String> roleCodes = new ArrayList<String>(loginUser.getRoleCodes());
        List<String> permissionCodes = new ArrayList<String>(loginUser.getPermissionCodes());
        Collections.sort(roleCodes);
        Collections.sort(permissionCodes);

        MeVO vo = new MeVO();
        vo.setUserId(loginUser.getUserId());
        vo.setUsername(loginUser.getUsername());
        vo.setNickName(loginUser.getNickName());
        vo.setRoles(roleCodes);
        vo.setPermissions(permissionCodes);
        return vo;
    }

    /**
     * 登出用户。
     */
    @Override
    public void logout(Long userId) {
        accessService.bumpTokenVersion(userId);
    }

    /**
     * 修改密码。
     *
     * 修改成功后提升 Token 版本，要求用户重新登录。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BusinessException(400, "旧密码不正确");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        int rows = sysUserMapper.update(user);
        if (rows != 1) {
            throw new BusinessException("修改密码失败");
        }
        accessService.bumpTokenVersion(userId);
    }
}
