package com.lvwyh.reviewx.web.service.apikey.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.apikey.SysUserApiKey;
import com.lvwyh.reviewx.web.mapper.apikey.SysUserApiKeyMapper;
import com.lvwyh.reviewx.web.security.LoginUser;
import com.lvwyh.reviewx.web.service.apikey.ApiKeyAuthService;
import com.lvwyh.reviewx.web.service.auth.AccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * API Key 认证服务实现。
 */
@Service
public class ApiKeyAuthServiceImpl extends ApiKeySecretSupport implements ApiKeyAuthService {

    private final SysUserApiKeyMapper sysUserApiKeyMapper;
    private final AccessService accessService;

    public ApiKeyAuthServiceImpl(SysUserApiKeyMapper sysUserApiKeyMapper,
                                 AccessService accessService,
                                 JwtProperties jwtProperties) {
        super(jwtProperties);
        this.sysUserApiKeyMapper = sysUserApiKeyMapper;
        this.accessService = accessService;
    }

    /**
     * 校验 API Key 并返回登录用户上下文。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginUser authenticate(String apiKey) {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException(401, "缺少API Key");
        }
        String prefix = extractPrefix(apiKey);
        if (!StringUtils.hasText(prefix)) {
            throw new BusinessException(401, "API Key格式错误");
        }

        SysUserApiKey entity = sysUserApiKeyMapper.selectByPrefix(prefix);
        if (entity == null || !constantTimeEquals(hmacSha256(apiKey), entity.getApiKeyHash())) {
            throw new BusinessException(401, "API Key无效");
        }
        if (!Integer.valueOf(1).equals(entity.getApiKeyStatus())) {
            throw new BusinessException(401, "API Key已禁用");
        }
        LocalDateTime now = LocalDateTime.now();
        if (entity.getExpireTime() != null && entity.getExpireTime().isBefore(now)) {
            throw new BusinessException(401, "API Key已过期");
        }

        sysUserApiKeyMapper.updateLastUsedTime(entity.getApiKeyId(), now);
        return accessService.loadLoginUser(entity.getUserId());
    }
}
