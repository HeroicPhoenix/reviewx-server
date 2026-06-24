package com.lvwyh.reviewx.web.mapper.apikey;

import com.lvwyh.reviewx.web.entity.apikey.SysUserApiKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 API Key Mapper。
 */
@Mapper
public interface SysUserApiKeyMapper {

    /** 新增 API Key 记录，插入后回填 apiKeyId。 */
    int insert(SysUserApiKey apiKey);

    /** 按前缀查询 API Key，用于认证时快速定位。 */
    SysUserApiKey selectByPrefix(@Param("apiKeyPrefix") String apiKeyPrefix);

    /** 查询当前用户创建的 API Key 列表。 */
    List<SysUserApiKey> selectByUserId(@Param("userId") Long userId);

    /** 查询当前用户名下指定 API Key。 */
    SysUserApiKey selectByIdAndUserId(@Param("apiKeyId") Long apiKeyId, @Param("userId") Long userId);

    /** 禁用 API Key。 */
    int disable(@Param("apiKeyId") Long apiKeyId,
                @Param("userId") Long userId,
                @Param("updateTime") LocalDateTime updateTime);

    /** 删除 API Key。 */
    int delete(@Param("apiKeyId") Long apiKeyId, @Param("userId") Long userId);

    /** 更新最后使用时间。 */
    int updateLastUsedTime(@Param("apiKeyId") Long apiKeyId,
                           @Param("lastUsedTime") LocalDateTime lastUsedTime);
}
