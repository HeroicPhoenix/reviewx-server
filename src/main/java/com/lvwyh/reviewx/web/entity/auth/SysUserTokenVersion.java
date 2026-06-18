package com.lvwyh.reviewx.web.entity.auth;

public class SysUserTokenVersion {

    private Long userId;
    private Integer tokenVersion;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(Integer tokenVersion) { this.tokenVersion = tokenVersion; }
}
