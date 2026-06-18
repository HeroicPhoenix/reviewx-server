package com.lvwyh.reviewx.web.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色接口权限关系 Mapper。
 *
 * 用于把系统内置权限码授权给 ADMIN 角色。
 */
@Mapper
public interface SysRoleApiPermissionMapper {

    /** 新增角色与接口权限绑定关系。 */
    int insert(@Param("roleId") Long roleId, @Param("apiId") Long apiId);

    /** 判断指定角色是否已拥有指定接口权限。 */
    long countByRoleIdAndApiId(@Param("roleId") Long roleId, @Param("apiId") Long apiId);
}
