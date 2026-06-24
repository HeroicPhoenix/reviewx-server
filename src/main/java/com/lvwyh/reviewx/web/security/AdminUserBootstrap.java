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

/**
 * 默认管理员初始化器。
 *
 * 应用启动时自动确保 admin 用户、ADMIN 角色、接口权限码和绑定关系存在，
 * 这样新环境建表后可以直接使用 admin / 123456 登录。
 */
@Component
public class AdminUserBootstrap implements ApplicationRunner {

    private static final Logger log = LogManager.getLogger(AdminUserBootstrap.class);
    /** 默认管理员用户名。 */
    private static final String ADMIN_USERNAME = "admin";
    /** 默认管理员初始密码，首次部署后建议立即修改。 */
    private static final String ADMIN_PASSWORD = "123456";
    /** 默认管理员角色编码。 */
    private static final String ADMIN_ROLE_CODE = "ADMIN";
    /** 默认普通用户角色编码。 */
    private static final String NORMAL_ROLE_CODE = "USER";

    /**
     * 第一版系统内置权限码定义。
     *
     * 每一行依次为：权限码、权限名称、接口路径、HTTP 方法。
     */
    private static final String[][] API_DEFINITIONS = new String[][]{
            {"auth:me", "查询当前登录人", "/api/auth/me", "GET"},
            {"auth:logout", "退出登录", "/api/auth/logout", "POST"},
            {"auth:change-password", "修改密码", "/api/auth/changePassword", "POST"},
            {"question:view", "查看题目", "/api/question/detail", "GET"},
            {"question:search", "搜索题目", "/api/question/search", "GET"},
            {"question:import", "导入题目", "/api/question/importFromDocsZip", "POST"},
            {"practice:question", "刷题取题", "/api/practice/**", "GET"},
            {"practice:submit", "提交答案", "/api/practice/submitAnswer", "POST"},
            {"answer-record:view", "查看答题记录", "/api/answerRecord/**", "GET"},
            {"api-key:create", "创建API Key", "/api/apiKey/create", "POST"},
            {"api-key:list", "查询API Key列表", "/api/apiKey/list", "GET"},
            {"api-key:disable", "禁用API Key", "/api/apiKey/disable", "POST"},
            {"api-key:delete", "删除API Key", "/api/apiKey/delete", "POST"},
            {"user:manage", "管理用户", "/api/user/**", "*"}
    };

    /**
     * 普通用户默认拥有的权限码。
     */
    private static final String[] NORMAL_USER_API_CODES = new String[]{
            "auth:me",
            "auth:logout",
            "auth:change-password",
            "question:view",
            "question:search",
            "question:import",
            "practice:question",
            "practice:submit",
            "answer-record:view",
            "api-key:create",
            "api-key:list",
            "api-key:disable",
            "api-key:delete"
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

    /**
     * Spring Boot 启动完成后执行初始化。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        SysRole adminRole = ensureRole(ADMIN_ROLE_CODE, "系统管理员", 0);
        SysRole normalRole = ensureRole(NORMAL_ROLE_CODE, "普通用户", 0);
        ensureApiPermissions(adminRole.getRoleId(), normalRole.getRoleId());
        SysUser user = ensureAdminUser();
        ensureUserRole(user.getUserId(), adminRole.getRoleId());
        if (sysUserTokenVersionMapper.selectByUserId(user.getUserId()) == null) {
            sysUserTokenVersionMapper.insert(user.getUserId(), 1);
        }
    }

    /**
     * 确保默认管理员用户存在。
     */
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

    /**
     * 确保 ADMIN 角色存在。
     */
    private SysRole ensureRole(String roleCode, String roleName, Integer isDelete) {
        SysRole existed = sysRoleMapper.selectByRoleCode(roleCode);
        if (existed != null) {
            return existed;
        }
        SysRole role = new SysRole();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setRoleStatus(1);
        role.setIsDelete(isDelete);
        sysRoleMapper.insert(role);
        return role;
    }

    /**
     * 确保所有内置权限码存在，并绑定到 ADMIN 角色。
     */
    private void ensureApiPermissions(Long adminRoleId, Long normalRoleId) {
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
            if (sysRoleApiPermissionMapper.countByRoleIdAndApiId(adminRoleId, api.getApiId()) == 0) {
                sysRoleApiPermissionMapper.insert(adminRoleId, api.getApiId());
            }
            if (isNormalUserApi(definition[0])
                    && sysRoleApiPermissionMapper.countByRoleIdAndApiId(normalRoleId, api.getApiId()) == 0) {
                sysRoleApiPermissionMapper.insert(normalRoleId, api.getApiId());
            }
        }
    }

    private boolean isNormalUserApi(String apiCode) {
        for (String normalUserApiCode : NORMAL_USER_API_CODES) {
            if (normalUserApiCode.equals(apiCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 确保 admin 用户绑定 ADMIN 角色。
     */
    private void ensureUserRole(Long userId, Long roleId) {
        if (sysUserRoleMapper.countByUserIdAndRoleId(userId, roleId) == 0) {
            sysUserRoleMapper.insert(userId, roleId);
        }
    }
}
