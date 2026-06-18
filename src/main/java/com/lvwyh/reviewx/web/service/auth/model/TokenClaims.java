package com.lvwyh.reviewx.web.service.auth.model;

/**
 * JWT 中承载的业务声明。
 *
 * 只保存认证所需的最小信息，权限集合不放进 Token，而是每次请求从数据库加载。
 */
public class TokenClaims {

    /** 用户主键。 */
    private Long userId;
    /** 用户名。 */
    private String username;
    /** Token 版本号。 */
    private Integer tokenVersion;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getTokenVersion() { return tokenVersion; }
    public void setTokenVersion(Integer tokenVersion) { this.tokenVersion = tokenVersion; }
}
