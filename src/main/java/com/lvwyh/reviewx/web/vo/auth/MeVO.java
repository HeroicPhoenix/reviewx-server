package com.lvwyh.reviewx.web.vo.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MeVO implements Serializable {

    private Long userId;
    private String username;
    private String nickName;
    private List<String> roles = new ArrayList<String>();
    private List<String> permissions = new ArrayList<String>();

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}
