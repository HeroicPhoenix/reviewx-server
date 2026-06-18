package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统接口权限 Mapper。
 *
 * 负责权限码初始化、按编码查询权限，以及加载用户权限码集合。
 */
@Mapper
public interface SysApiMapper {

    /** 新增接口权限，插入后回填 apiId。 */
    int insert(SysApi api);

    /** 按权限编码查询接口权限。 */
    SysApi selectByApiCode(@Param("apiCode") String apiCode);

    /** 查询某个用户拥有的接口权限码列表。 */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
