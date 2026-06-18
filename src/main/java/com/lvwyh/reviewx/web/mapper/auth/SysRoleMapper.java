package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色 Mapper。
 *
 * 负责角色初始化、按编码查询角色，以及加载用户角色编码。
 */
@Mapper
public interface SysRoleMapper {

    /** 新增角色，插入后回填 roleId。 */
    int insert(SysRole role);

    /** 按角色编码查询角色。 */
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    /** 查询某个用户拥有的角色编码列表。 */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
