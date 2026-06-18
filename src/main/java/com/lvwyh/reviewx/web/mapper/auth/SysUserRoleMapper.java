package com.lvwyh.reviewx.web.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserRoleMapper {

    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    long countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
