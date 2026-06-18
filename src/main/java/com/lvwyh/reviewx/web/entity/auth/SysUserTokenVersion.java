package com.lvwyh.reviewx.web.entity.auth;

/**
 * 用户 Token 版本实体。
 *
 * 映射 SYS_USER_TOKEN_VERSION 表，用于在登出或改密时使历史 Token 失效。
 */
public class SysUserTokenVersion {

    /** 用户主键。 */
    private Long userId;
    /** 当前有效 Token 版本号。 */
    private Integer tokenVersion;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(Integer tokenVersion) { this.tokenVersion = tokenVersion; }
}
