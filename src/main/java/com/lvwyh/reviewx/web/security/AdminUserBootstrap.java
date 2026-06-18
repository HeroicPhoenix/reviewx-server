package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.entity.auth.SysApi;
import com.lvwyh.reviewx.web.entity.auth.SysRole;
import com.lvwyh.reviewx.web.entity.auth.SysUser;
import com.lvwyh.reviewx.web.mapper.auth.SysApiMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysRoleApiPermissionMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysRoleMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserRoleMapper;
import com.lvwyh.reviewx.web.mapper.auth.SysUserTokenVersionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class AdminUserBootstrap implements ApplicationRunner {

    private static final Logger log = LogManager.getLogger(AdminUserBootstrap.class);
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123456";
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    private static final String[][] API_DEFINITIONS = new String[][]{
            {"auth:me", "查询当前登录人", "/api/auth/me", "GET"},
            {"auth:logout", "退出登录", "/api/auth/logout", "POST"},
            {"question:view", "查看题目", "/api/question/detail", "GET"},
            {"question:search", "搜索题目", "/api/question/search", "GET"},
            {"practice:question", "刷题取题", "/api/practice/**", "GET"},
            {"practice:submit", "提交答案", "/api/practice/submitAnswer", "POST"},
            {"answer-record:view", "查看答题记录", "/api/answerRecord/**", "GET"}
    };

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysApiMapper sysApiMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleApiPermissionMapper sysRoleApiPermissionMapper;
    private final SysUserTokenVersionMapper sysUserTokenVersionMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminUserBootstrap(SysUserMapper sysUserMapper,
                              SysRoleMapper sysRoleMapper,
                              SysApiMapper sysApiMapper,
                              SysUserRoleMapper sysUserRoleMapper,
                              SysRoleApiPermissionMapper sysRoleApiPermissionMapper,
                              SysUserTokenVersionMapper sysUserTokenVersionMapper,
                              PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysApiMapper = sysApiMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleApiPermissionMapper = sysRoleApiPermissionMapper;
        this.sysUserTokenVersionMapper = sysUserTokenVersionMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        SysRole role = ensureAdminRole();
        ensureApiPermissions(role.getRoleId());
        SysUser user = ensureAdminUser();
        ensureUserRole(user.getUserId(), role.getRoleId());
        if (sysUserTokenVersionMapper.selectByUserId(user.getUserId()) == null) {
            sysUserTokenVersionMapper.insert(user.getUserId(), 1);
        }
    }

    private SysUser ensureAdminUser() {
        SysUser existed = sysUserMapper.selectByUsername(ADMIN_USERNAME);
        if (existed != null) {
            return existed;
        }
        LocalDateTime now = LocalDateTime.now();
        SysUser user = new SysUser();
        user.setUsername(ADMIN_USERNAME);
        user.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        user.setNickName("超级管理员");
        user.setUserStatus(1);
        user.setIsDelete(0);
        user.setIsAllowRoleChange(0);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        sysUserMapper.insert(user);
        log.info("Bootstrap admin user created: username={}", ADMIN_USERNAME);
        return user;
    }

    private SysRole ensureAdminRole() {
        SysRole existed = sysRoleMapper.selectByRoleCode(ADMIN_ROLE_CODE);
        if (existed != null) {
            return existed;
        }
        SysRole role = new SysRole();
        role.setRoleCode(ADMIN_ROLE_CODE);
        role.setRoleName("系统管理员");
        role.setRoleStatus(1);
        role.setIsDelete(0);
        sysRoleMapper.insert(role);
        return role;
    }

    private void ensureApiPermissions(Long roleId) {
        for (String[] definition : API_DEFINITIONS) {
            SysApi api = sysApiMapper.selectByApiCode(definition[0]);
            if (api == null) {
                api = new SysApi();
                api.setApiCode(definition[0]);
                api.setApiName(definition[1]);
                api.setApiPath(definition[2]);
                api.setHttpMethod(definition[3]);
                api.setStatus(1);
                sysApiMapper.insert(api);
            }
            if (sysRoleApiPermissionMapper.countByRoleIdAndApiId(roleId, api.getApiId()) == 0) {
                sysRoleApiPermissionMapper.insert(roleId, api.getApiId());
            }
        }
    }

    private void ensureUserRole(Long userId, Long roleId) {
        if (sysUserRoleMapper.countByUserIdAndRoleId(userId, roleId) == 0) {
            sysUserRoleMapper.insert(userId, roleId);
        }
    }
}
