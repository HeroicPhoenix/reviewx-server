package com.lvwyh.reviewx.web.vo.auth;

import java.io.Serializable;

public class LoginVO implements Serializable {

    private String accessToken;
    private String tokenType;
    private Long expireIn;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getExpireIn() { return expireIn; }
    public void setExpireIn(Long expireIn) { this.expireIn = expireIn; }
}
