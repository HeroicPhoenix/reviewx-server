package com.lvwyh.reviewx.web.vo.apikey;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建 API Key 返回对象。
 *
 * 完整 API Key 只在创建时返回一次，后续列表接口不会再返回明文。
 */
public class ApiKeyCreateVO implements Serializable {

    /** API Key ID。 */
    private Long apiKeyId;
    /** API Key 名称。 */
    private String apiKeyName;
    /** 完整 API Key 明文，只返回一次。 */
    private String apiKey;
    /** API Key 前缀。 */
    private String apiKeyPrefix;
    /** 过期时间。 */
    private LocalDateTime expireTime;

    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }
    public String getApiKeyName() { return apiKeyName; }
    public void setApiKeyName(String apiKeyName) { this.apiKeyName = apiKeyName; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiKeyPrefix() { return apiKeyPrefix; }
    public void setApiKeyPrefix(String apiKeyPrefix) { this.apiKeyPrefix = apiKeyPrefix; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
}
