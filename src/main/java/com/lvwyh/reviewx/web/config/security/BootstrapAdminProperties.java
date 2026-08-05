package com.lvwyh.reviewx.web.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 新环境管理员初始化配置，默认关闭，避免产生公开的默认账号。 */
@Component
@ConfigurationProperties(prefix = "reviewx.bootstrap-admin")
public class BootstrapAdminProperties {

    private boolean enabled;
    private String username;
    private String password;
    private String nickName = "超级管理员";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
}
