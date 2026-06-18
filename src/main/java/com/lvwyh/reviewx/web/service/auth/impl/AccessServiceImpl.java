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

/**
 * 登录访问上下文服务实现。
 *
 * 每次认证通过后都会加载用户状态、角色、权限和 Token 版本，保证权限变更能及时生效。
 */
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

    /**
     * 加载完整登录用户上下文。
     *
     * 如果用户不存在、被删除或禁用，直接返回 401，阻止后续业务执行。
     */
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

    /**
     * 获取 Token 版本；首次登录时自动初始化为 1。
     */
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

    /**
     * Token 版本递增。
     *
     * 登出或修改密码后，旧 Token 中的版本号会小于数据库版本号，从而被拦截器拒绝。
     */
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

    /**
     * 将数据库查询出的字符串列表转换为去重集合，并过滤空值。
     */
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
