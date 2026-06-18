package com.lvwyh.reviewx.web.vo.auth;

import java.io.Serializable;

/**
 * 登录成功返回对象。
 *
 * 前端拿到 accessToken 后需要按 Authorization: Bearer <token> 的格式访问后续接口。
 */
public class LoginVO implements Serializable {

    /** JWT 访问令牌。 */
    private String accessToken;
    /** 令牌类型，固定为 Bearer。 */
    private String tokenType;
    /** 令牌有效期，单位秒。 */
    private Long expireIn;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getExpireIn() { return expireIn; }
    public void setExpireIn(Long expireIn) { this.expireIn = expireIn; }
}
