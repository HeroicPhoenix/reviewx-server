package com.lvwyh.reviewx.web.entity.auth;

import java.time.LocalDateTime;

public class SysUser {

    private Long userId;
    private String username;
    private String passwordHash;
    private String nickName;
    private Integer userStatus;
    private Integer isDelete;
    private Integer isAllowRoleChange;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public Integer getUserStatus() { return userStatus; }
    public void setUserStatus(Integer userStatus) { this.userStatus = userStatus; }
    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
    public Integer getIsAllowRoleChange() { return isAllowRoleChange; }
    public void setIsAllowRoleChange(Integer isAllowRoleChange) { this.isAllowRoleChange = isAllowRoleChange; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
