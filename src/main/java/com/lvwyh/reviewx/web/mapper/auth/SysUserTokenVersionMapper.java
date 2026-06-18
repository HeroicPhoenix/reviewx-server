package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysUserTokenVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Token 版本 Mapper。
 *
 * Token 版本用于服务端主动失效旧 Token，例如登出和修改密码。
 */
@Mapper
public interface SysUserTokenVersionMapper {

    /** 查询用户当前 Token 版本。 */
    SysUserTokenVersion selectByUserId(@Param("userId") Long userId);

    /** 初始化用户 Token 版本。 */
    int insert(@Param("userId") Long userId, @Param("tokenVersion") Integer tokenVersion);

    /** 更新用户 Token 版本。 */
    int updateTokenVersion(@Param("userId") Long userId, @Param("tokenVersion") Integer tokenVersion);
}
