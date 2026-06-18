package com.lvwyh.reviewx.web.security;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LoginUser implements Serializable {

    private Long userId;

    private String username;

    private String nickName;

    private Integer tokenVersion;

    private Set<String> roleCodes = new HashSet<String>();

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
