package com.lvwyh.reviewx.web.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 当前登录用户上下文模型。
 *
 * 该对象由 JWT 认证拦截器加载并写入 ThreadLocal，业务层通过 LoginUserContext 获取。
 */
public class LoginUser implements Serializable {

    /** 用户主键。 */
    private Long userId;

    /** 登录用户名。 */
    private String username;

    /** 展示昵称。 */
    private String nickName;

    /** Token 版本号，用于登出或改密后让旧 Token 失效。 */
    private Integer tokenVersion;

    /** 当前用户拥有的角色编码集合。 */
    private Set<String> roleCodes = new HashSet<String>();

    /** 当前用户拥有的接口权限编码集合。 */
    private Set<String> permissionCodes = new HashSet<String>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(Integer tokenVersion) {
        this.tokenVersion = tokenVersion;
    }

    public Set<String> getRoleCodes() {
        return Collections.unmodifiableSet(roleCodes);
    }

    public void setRoleCodes(Set<String> roleCodes) {
        this.roleCodes = roleCodes == null ? new HashSet<String>() : new HashSet<String>(roleCodes);
    }

    public Set<String> getPermissionCodes() {
        return Collections.unmodifiableSet(permissionCodes);
    }

    public void setPermissionCodes(Set<String> permissionCodes) {
        this.permissionCodes = permissionCodes == null ? new HashSet<String>() : new HashSet<String>(permissionCodes);
    }
}
