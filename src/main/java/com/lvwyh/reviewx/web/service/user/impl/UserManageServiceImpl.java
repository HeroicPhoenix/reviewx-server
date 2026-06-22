package com.lvwyh.reviewx.web.service.user.impl;

import com.lvwyh.reviewx.web.ao.user.CreateUserAO;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.auth.SysRole;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysRoleMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserRoleMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserTokenVersionMapper;
import com.lvwyh.reviewx.web.service.user.UserManageService;
import com.lvwyh.reviewx.web.vo.user.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户管理服务实现。
 */
@Service
public class UserManageServiceImpl implements UserManageService {

    private static final String NORMAL_ROLE_CODE = "USER";

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserTokenVersionMapper sysUserTokenVersionMapper;
    private final PasswordEncoder passwordEncoder;

    public UserManageServiceImpl(SysUserMapper sysUserMapper,
                                 SysRoleMapper sysRoleMapper,
                                 SysUserRoleMapper sysUserRoleMapper,
                                 SysUserTokenVersionMapper sysUserTokenVersionMapper,
                                 PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysUserTokenVersionMapper = sysUserTokenVersionMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createNormalUser(CreateUserAO ao) {
        String username = ao.getUsername() == null ? null : ao.getUsername().trim();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(ao.getPassword()));
        user.setNickName(StringUtils.hasText(ao.getNickName()) ? ao.getNickName().trim() : username);
        user.setUserStatus(1);
        user.setIsDelete(1);
        user.setIsAllowRoleChange(1);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        int rows = sysUserMapper.insert(user);
        if (rows != 1) {
            throw new BusinessException("新增用户失败");
        }

        SysRole role = sysRoleMapper.selectByRoleCode(NORMAL_ROLE_CODE);
        if (role == null) {
            throw new BusinessException(500, "普通用户角色未初始化");
        }
        sysUserRoleMapper.insert(user.getUserId(), role.getRoleId());
        sysUserTokenVersionMapper.insert(user.getUserId(), 1);
        return toVO(user);
    }

    @Override
    public List<UserVO> listUsers() {
        List<SysUser> users = sysUserMapper.selectActiveUsers();
        List<UserVO> result = new ArrayList<UserVO>();
        if (users == null) {
            return result;
        }
        for (SysUser user : users) {
            result.add(toVO(user));
        }
        return result;
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setNickName(user.getNickName());
        vo.setUserStatus(user.getUserStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
