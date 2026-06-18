package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysUserTokenVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserTokenVersionMapper {

    SysUserTokenVersion selectByUserId(@Param("userId") Long userId);

    int insert(@Param("userId") Long userId, @Param("tokenVersion") Integer tokenVersion);

    int updateTokenVersion(@Param("userId") Long userId, @Param("tokenVersion") Integer tokenVersion);
}
