package com.lvwyh.reviewx.web.service.auth.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.entity.auth.SysUserTokenVersion;
import com.lvwyh.reviewx.web.mapper.auth.SysApiMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysRoleMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserTokenVersionMapper;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AccessServiceImpl implements AccessService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysApiMapper sysApiMapper;
    private final SysUserTokenVersionMapper sysUserTokenVersionMapper;

    public AccessServiceImpl(SysUserMapper sysUserMapper,
                             SysRoleMapper sysRoleMapper,
                             SysApiMapper sysApiMapper,
                             SysUserTokenVersionMapper sysUserTokenVersionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysApiMapper = sysApiMapper;
        this.sysUserTokenVersionMapper = sysUserTokenVersionMapper;
    }

    @Override
    public LoginUser loadLoginUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || Integer.valueOf(3).equals(user.getUserStatus())) {
            throw new BusinessException(401, "用户不存在或已删除");
        }
        if (!Integer.valueOf(1).equals(user.getUserStatus())) {
            throw new BusinessException(401, "用户已被禁用");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickName(user.getNickName());
        loginUser.setTokenVersion(getTokenVersionOrInit(userId));
        loginUser.setRoleCodes(toSet(sysRoleMapper.selectRoleCodesByUserId(userId)));
        loginUser.setPermissionCodes(toSet(sysApiMapper.selectPermissionCodesByUserId(userId)));
        return loginUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer getTokenVersionOrInit(Long userId) {
        SysUserTokenVersion existed = sysUserTokenVersionMapper.selectByUserId(userId);
        if (existed != null) {
            return existed.getTokenVersion();
        }
        sysUserTokenVersionMapper.insert(userId, 1);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int bumpTokenVersion(Long userId) {
        int next = getTokenVersionOrInit(userId) + 1;
        int rows = sysUserTokenVersionMapper.updateTokenVersion(userId, next);
        if (rows != 1) {
            throw new BusinessException("更新用户令牌版本失败");
        }
        return next;
    }

    private Set<String> toSet(List<String> values) {
        Set<String> result = new HashSet<String>();
        if (values == null) {
            return result;
        }
        for (String value : values) {
            if (value != null && value.trim().length() > 0) {
                result.add(value);
            }
        }
        return result;
    }
}
