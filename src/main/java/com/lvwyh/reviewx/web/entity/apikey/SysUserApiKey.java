package com.lvwyh.reviewx.web.entity.apikey;

import java.time.LocalDateTime;

/**
 * 用户 API Key 实体。
 *
 * 映射 SYS_USER_API_KEY 表。数据库只保存前缀和 HMAC 哈希，不保存完整 API Key 明文。
 */
public class SysUserApiKey {

    /** API Key 主键。 */
    private Long apiKeyId;
    /** 归属用户 ID。 */
    private Long userId;
    /** API Key 名称。 */
    private String apiKeyName;
    /** API Key 前缀，用于快速定位记录。 */
    private String apiKeyPrefix;
    /** 完整 API Key 的 HMAC-SHA256 哈希。 */
    private String apiKeyHash;
    /** 状态：1启用、2禁用。 */
    private Integer apiKeyStatus;
    /** 过期时间，空表示不过期。 */
    private LocalDateTime expireTime;
    /** 最后使用时间。 */
    private LocalDateTime lastUsedTime;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 更新时间。 */
    private LocalDateTime updateTime;

    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getApiKeyName() { return apiKeyName; }
    public void setApiKeyName(String apiKeyName) { this.apiKeyName = apiKeyName; }
    public String getApiKeyPrefix() { return apiKeyPrefix; }
    public void setApiKeyPrefix(String apiKeyPrefix) { this.apiKeyPrefix = apiKeyPrefix; }
    public String getApiKeyHash() { return apiKeyHash; }
    public void setApiKeyHash(String apiKeyHash) { this.apiKeyHash = apiKeyHash; }
    public Integer getApiKeyStatus() { return apiKeyStatus; }
    public void setApiKeyStatus(Integer apiKeyStatus) { this.apiKeyStatus = apiKeyStatus; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
    public LocalDateTime getLastUsedTime() { return lastUsedTime; }
    public void setLastUsedTime(LocalDateTime lastUsedTime) { this.lastUsedTime = lastUsedTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
