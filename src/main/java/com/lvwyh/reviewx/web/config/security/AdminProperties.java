package com.lvwyh.reviewx.web.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 超级管理员创建与登录开关配置。 */
@Component
@ConfigurationProperties(prefix = "reviewx.admin")
public class AdminProperties {

    /** 系统不存在可用 ADMIN 用户时，是否允许自动创建一个管理员。 */
    private boolean createIfMissing;

    private String username;
    private String password;
    private String nickName = "超级管理员";

    /** 是否允许 ADMIN 角色登录；默认允许，关闭后所有来源均不能登录。 */
    private boolean loginEnabled = true;

    public boolean isCreateIfMissing() { return createIfMissing; }
    public void setCreateIfMissing(boolean createIfMissing) { this.createIfMissing = createIfMissing; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public boolean isLoginEnabled() { return loginEnabled; }
    public void setLoginEnabled(boolean loginEnabled) { this.loginEnabled = loginEnabled; }
}
