package com.lvwyh.reviewx.web.ao.apikey;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建 API Key 入参。
 */
@Schema(description = "创建API Key入参")
public class ApiKeyCreateAO implements Serializable {

    /** API Key 名称，用于用户区分用途。 */
    @NotBlank(message = "API Key名称不能为空")
    @Size(max = 100, message = "API Key名称长度不能超过100")
    private String apiKeyName;

    /** 过期时间，空表示不过期。 */
    private LocalDateTime expireTime;

    public String getApiKeyName() { return apiKeyName; }
    public void setApiKeyName(String apiKeyName) { this.apiKeyName = apiKeyName; }
    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
}
