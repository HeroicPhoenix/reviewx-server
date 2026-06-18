package com.lvwyh.reviewx.web.service.auth.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import com.lvwyh.reviewx.web.service.auth.AuthService;
import com.lvwyh.reviewx.web.service.auth.TokenService;
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

    private final SysUserMapper sysUserMapper;
    private final AccessService accessService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(SysUserMapper sysUserMapper,
                           AccessService accessService,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder,
                           JwtProperties jwtProperties) {
        this.sysUserMapper = sysUserMapper;
        this.accessService = accessService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 登录入口。
     *
     * 校验用户名、用户状态和密码，全部通过后签发 JWT。
     */
    @Override
    public LoginVO login(String username, String password) {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || !Integer.valueOf(1).equals(user.getUserStatus())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        Integer tokenVersion = accessService.getTokenVersionOrInit(user.getUserId());
        LoginVO vo = new LoginVO();
        vo.setAccessToken(tokenService.generate(user.getUserId(), user.getUsername(), tokenVersion));
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
