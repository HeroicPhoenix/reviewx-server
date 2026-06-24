package com.lvwyh.reviewx.web.ao.apikey;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * API Key ID 入参。
 *
 * 禁用和删除接口共用。
 */
@Schema(description = "API Key ID入参")
public class ApiKeyIdAO implements Serializable {

    /** API Key ID。 */
    @NotNull(message = "API Key ID不能为空")
    private Long apiKeyId;

    public Long getApiKeyId() { return apiKeyId; }
    public void setApiKeyId(Long apiKeyId) { this.apiKeyId = apiKeyId; }
}
