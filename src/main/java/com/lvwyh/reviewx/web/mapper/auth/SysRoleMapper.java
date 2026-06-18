package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    int insert(SysRole role);

    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
