package com.lvwyh.reviewx.web.service.apikey;

import com.lvwyh.reviewx.web.ao.apikey.ApiKeyCreateAO;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyCreateVO;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyVO;

import java.util.List;

/**
 * API Key 管理服务。
 */
public interface ApiKeyService {

    /** 创建 API Key。 */
    ApiKeyCreateVO create(Long userId, ApiKeyCreateAO ao);

    /** 查询当前用户 API Key 列表。 */
    List<ApiKeyVO> list(Long userId);

    /** 禁用当前用户 API Key。 */
    void disable(Long userId, Long apiKeyId);

    /** 删除当前用户 API Key。 */
    void delete(Long userId, Long apiKeyId);
}
