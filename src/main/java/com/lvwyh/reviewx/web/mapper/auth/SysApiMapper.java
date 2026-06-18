package com.lvwyh.reviewx.web.mapper.auth;

import com.lvwyh.reviewx.web.entity.auth.SysApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysApiMapper {

    int insert(SysApi api);

    SysApi selectByApiCode(@Param("apiCode") String apiCode);

    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
