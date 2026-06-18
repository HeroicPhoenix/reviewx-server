package com.lvwyh.reviewx.web.entity.auth;

public class SysRole {

    private Long roleId;
    private String roleCode;
    private String roleName;
    private Integer roleStatus;
    private Integer isDelete;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public Integer getRoleStatus() { return roleStatus; }
    public void setRoleStatus(Integer roleStatus) { this.roleStatus = roleStatus; }
    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
}
