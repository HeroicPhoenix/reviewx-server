package com.lvwyh.reviewx.web.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 超级管理员创建与来源 IP 白名单配置。 */
@Component
@ConfigurationProperties(prefix = "reviewx.admin")
public class AdminProperties {

    /** 系统不存在可用 ADMIN 用户时，是否允许自动创建一个管理员。 */
    private boolean createIfMissing;

    private String username;
    private String password;
    private String nickName = "超级管理员";

    /** ADMIN 角色允许登录和访问的客户端 IP 列表；空列表表示全部拒绝。 */
    private List<String> allowedIpAddresses = new ArrayList<String>();

    public boolean isCreateIfMissing() { return createIfMissing; }
    public void setCreateIfMissing(boolean createIfMissing) { this.createIfMissing = createIfMissing; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public List<String> getAllowedIpAddresses() { return allowedIpAddresses; }
    public void setAllowedIpAddresses(List<String> allowedIpAddresses) {
        this.allowedIpAddresses = allowedIpAddresses == null
                ? new ArrayList<String>() : new ArrayList<String>(allowedIpAddresses);
    }
}
