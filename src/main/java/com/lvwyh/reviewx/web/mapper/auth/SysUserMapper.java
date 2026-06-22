package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户 Mapper。
 *
 * 负责 SYS_USER 表的基础读写，主要服务于登录、改密和默认管理员初始化。
 */
@Mapper
public interface SysUserMapper {

    /** 新增用户，插入后回填 userId。 */
    int insert(SysUser user);

    /** 按用户 ID 查询用户。 */
    SysUser selectById(@Param("userId") Long userId);

    /** 按登录用户名查询用户，用于登录和唯一性判断。 */
    SysUser selectByUsername(@Param("username") String username);

    /** 查询未逻辑删除的用户列表。 */
    List<SysUser> selectActiveUsers();

    /** 更新用户基础信息和密码哈希。 */
    int update(SysUser user);
}
