package com.lvwyh.reviewx.web.vo.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 当前登录用户信息返回对象。
 *
 * 用于前端初始化登录态、展示用户信息以及判断可用权限。
 */
public class MeVO implements Serializable {

    /** 用户主键。 */
    private Long userId;
    /** 登录用户名。 */
    private String username;
    /** 用户昵称。 */
    private String nickName;
    /** 用户角色编码列表。 */
    private List<String> roles = new ArrayList<String>();
    /** 用户接口权限编码列表。 */
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
