package com.lvwyh.reviewx.web.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项。
 *
 * 对应 application.yml 中的 reviewx.jwt 配置，用于签发和校验登录令牌。
 */
@Component
@ConfigurationProperties(prefix = "reviewx.jwt")
public class JwtProperties {

    private static final String DEFAULT_SECRET = "ReviewXJwtSecretKeyForLocalDevelopmentOnly123456";
    /** JWT HMAC 签名密钥，生产环境必须使用强随机密钥。 */
    private String secret = DEFAULT_SECRET;

    /** Token 有效期，单位秒。 */
    private Long expireSeconds = 7200L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        if (secret != null && secret.trim().length() > 0) {
            this.secret = secret;
        }
    }

    public Long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(Long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
