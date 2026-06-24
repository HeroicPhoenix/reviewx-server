package com.lvwyh.reviewx.web.vo.apikey;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * API Key 列表返回对象。
 *
 * 不包含完整 API Key 明文，只展示前缀和状态信息。
 */
public class ApiKeyVO implements Serializable {

    /** API Key ID。 */
    private Long apiKeyId;
    /** API Key 名称。 */
    private String apiKeyName;
    /** API Key 前缀。 */
    private String apiKeyPrefix;
    /** 状态：1启用、2禁用。 */
    private Integer apiKeyStatus;
    /** 过期时间。 */
    private LocalDateTime expireTime;
    /** 最后使用时间。 */
    private LocalDateTime lastUsedTime;
    /** 创建时间。 */
    private LocalDateTime createTime;

    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }
    public String getApiKeyName() { return apiKeyName; }
    public void setApiKeyName(String apiKeyName) { this.apiKeyName = apiKeyName; }
    public String getApiKeyPrefix() { return apiKeyPrefix; }
    public void setApiKeyPrefix(String apiKeyPrefix) { this.apiKeyPrefix = apiKeyPrefix; }
    public Integer getApiKeyStatus() { return apiKeyStatus; }
    public void setApiKeyStatus(Integer apiKeyStatus) { this.apiKeyStatus = apiKeyStatus; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    public LocalDateTime getLastUsedTime() { return lastUsedTime; }
    public void setLastUsedTime(LocalDateTime lastUsedTime) { this.lastUsedTime = lastUsedTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
