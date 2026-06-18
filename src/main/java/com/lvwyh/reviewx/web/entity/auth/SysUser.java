package com.lvwyh.reviewx.web.entity.auth;

import java.time.LocalDateTime;

/**
 * 系统用户实体。
 *
 * 映射 SYS_USER 表，用于登录认证、用户状态校验和默认管理员初始化。
 */
public class SysUser {

    /** 用户主键。 */
    private Long userId;
    /** 登录用户名。 */
    private String username;
    /** BCrypt 密码哈希。 */
    private String passwordHash;
    /** 昵称。 */
    private String nickName;
    /** 用户状态：1正常、2禁用、3逻辑删除。 */
    private Integer userStatus;
    /** 是否允许删除：0不允许、1允许。 */
    private Integer isDelete;
    /** 是否允许修改角色：0不允许、1允许。 */
    private Integer isAllowRoleChange;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 更新时间。 */
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
