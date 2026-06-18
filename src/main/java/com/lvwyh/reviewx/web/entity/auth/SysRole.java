package com.lvwyh.reviewx.web.entity.auth;

/**
 * 系统角色实体。
 *
 * 映射 SYS_ROLE 表，当前主要用于 ADMIN 角色初始化和权限加载。
 */
public class SysRole {

    /** 角色主键。 */
    private Long roleId;
    /** 角色编码，例如 ADMIN。 */
    private String roleCode;
    /** 角色名称。 */
    private String roleName;
    /** 角色状态：1正常、2禁用、3逻辑删除。 */
    private Integer roleStatus;
    /** 是否允许删除：0不允许、1允许。 */
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
