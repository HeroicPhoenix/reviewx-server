package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {

    int insert(SysUser user);

    SysUser selectById(@Param("userId") Long userId);

    SysUser selectByUsername(@Param("username") String username);

    int update(SysUser user);
}
