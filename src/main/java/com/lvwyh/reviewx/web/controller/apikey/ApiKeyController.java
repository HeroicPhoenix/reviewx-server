package com.lvwyh.reviewx.web.controller.apikey;

import com.lvwyh.reviewx.web.ao.apikey.ApiKeyCreateAO;
import com.lvwyh.reviewx.web.ao.apikey.ApiKeyIdAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.apikey.ApiKeyService;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyCreateVO;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * API Key 管理控制器。
 */
@Tag(name = "API Key管理")
@Validated
@RestController
@RequestMapping("/api/apiKey")
public class ApiKeyController {

    private static final Logger log = LogManager.getLogger(ApiKeyController.class);

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * 创建 API Key。
     */
    @Operation(summary = "创建API Key")
    @RequirePermission("api-key:create")
    @PostMapping("/create")
    public ApiResponse<ApiKeyCreateVO> create(@Valid @RequestBody ApiKeyCreateAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Create API key request: userId={}, apiKeyName={}", userId, ao.getApiKeyName());
        return ApiResponse.success("创建成功，请立即保存API Key，后续将不再展示", apiKeyService.create(userId, ao));
    }

    /**
     * 查询当前用户 API Key 列表。
     */
    @Operation(summary = "查询API Key列表")
    @RequirePermission("api-key:list")
    @GetMapping("/list")
    public ApiResponse<List<ApiKeyVO>> list() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", apiKeyService.list(userId));
    }

    /**
     * 禁用 API Key。
     */
    @Operation(summary = "禁用API Key")
    @RequirePermission("api-key:disable")
    @PostMapping("/disable")
    public ApiResponse<Void> disable(@Valid @RequestBody ApiKeyIdAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        apiKeyService.disable(userId, ao.getApiKeyId());
        return ApiResponse.success("禁用成功", null);
    }

    /**
     * 删除 API Key。
     */
    @Operation(summary = "删除API Key")
    @RequirePermission("api-key:delete")
    @PostMapping("/delete")
    public ApiResponse<Void> delete(@Valid @RequestBody ApiKeyIdAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        apiKeyService.delete(userId, ao.getApiKeyId());
        return ApiResponse.success("删除成功", null);
    }
}
