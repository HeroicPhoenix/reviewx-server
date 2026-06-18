package com.lvwyh.reviewx.web.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysRoleApiPermissionMapper {

    int insert(@Param("roleId") Long roleId, @Param("apiId") Long apiId);

    long countByRoleIdAndApiId(@Param("roleId") Long roleId, @Param("apiId") Long apiId);
}
