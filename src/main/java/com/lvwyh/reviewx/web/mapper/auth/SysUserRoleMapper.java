package com.lvwyh.reviewx.web.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户角色关系 Mapper。
 *
 * 当前用于默认管理员与 ADMIN 角色绑定。
 */
@Mapper
public interface SysUserRoleMapper {

    /** 新增用户角色绑定关系。 */
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /** 判断指定用户和角色是否已经绑定。 */
    long countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
