package com.lvwyh.reviewx.web.service.apikey.impl;

import com.lvwyh.reviewx.web.ao.apikey.ApiKeyCreateAO;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.entity.apikey.SysUserApiKey;
import com.lvwyh.reviewx.web.mapper.apikey.SysUserApiKeyMapper;
import com.lvwyh.reviewx.web.service.apikey.ApiKeyService;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyCreateVO;
import com.lvwyh.reviewx.web.vo.apikey.ApiKeyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * API Key 管理服务实现。
 */
@Service
public class ApiKeyServiceImpl extends ApiKeySecretSupport implements ApiKeyService {

    private final SysUserApiKeyMapper sysUserApiKeyMapper;

    public ApiKeyServiceImpl(SysUserApiKeyMapper sysUserApiKeyMapper, JwtProperties jwtProperties) {
        super(jwtProperties);
        this.sysUserApiKeyMapper = sysUserApiKeyMapper;
    }

    /**
     * 创建 API Key。
     *
     * 完整 Key 只在本方法返回一次；数据库只保存 HMAC 哈希。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiKeyCreateVO create(Long userId, ApiKeyCreateAO ao) {
        LocalDateTime now = LocalDateTime.now();
        String prefix = generateUniquePrefix();
        String apiKey = generateApiKey(prefix);

        SysUserApiKey entity = new SysUserApiKey();
        entity.setUserId(userId);
        entity.setApiKeyName(ao.getApiKeyName());
        entity.setApiKeyPrefix(prefix);
        entity.setApiKeyHash(hmacSha256(apiKey));
        entity.setApiKeyStatus(1);
        entity.setExpireTime(ao.getExpireTime());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        int rows = sysUserApiKeyMapper.insert(entity);
        if (rows != 1) {
            throw new BusinessException("创建API Key失败");
        }

        ApiKeyCreateVO vo = new ApiKeyCreateVO();
        vo.setApiKeyId(entity.getApiKeyId());
        vo.setApiKeyName(entity.getApiKeyName());
        vo.setApiKey(apiKey);
        vo.setApiKeyPrefix(prefix);
        vo.setExpireTime(entity.getExpireTime());
        return vo;
    }

    @Override
    public List<ApiKeyVO> list(Long userId) {
        List<SysUserApiKey> entities = sysUserApiKeyMapper.selectByUserId(userId);
        List<ApiKeyVO> result = new ArrayList<ApiKeyVO>();
        if (entities == null) {
            return result;
        }
        for (SysUserApiKey entity : entities) {
            result.add(toVO(entity));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disable(Long userId, Long apiKeyId) {
        SysUserApiKey existed = sysUserApiKeyMapper.selectByIdAndUserId(apiKeyId, userId);
        if (existed == null) {
            throw new BusinessException(404, "API Key不存在");
        }
        int rows = sysUserApiKeyMapper.disable(apiKeyId, userId, LocalDateTime.now());
        if (rows != 1) {
            throw new BusinessException("禁用API Key失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long apiKeyId) {
        SysUserApiKey existed = sysUserApiKeyMapper.selectByIdAndUserId(apiKeyId, userId);
        if (existed == null) {
            throw new BusinessException(404, "API Key不存在");
        }
        int rows = sysUserApiKeyMapper.delete(apiKeyId, userId);
        if (rows != 1) {
            throw new BusinessException("删除API Key失败");
        }
    }

    private String generateUniquePrefix() {
        for (int i = 0; i < 10; i++) {
            String prefix = generatePrefix();
            if (sysUserApiKeyMapper.selectByPrefix(prefix) == null) {
                return prefix;
            }
        }
        throw new BusinessException(500, "生成API Key前缀失败");
    }

    private ApiKeyVO toVO(SysUserApiKey entity) {
        ApiKeyVO vo = new ApiKeyVO();
        vo.setApiKeyId(entity.getApiKeyId());
        vo.setApiKeyName(entity.getApiKeyName());
        vo.setApiKeyPrefix(entity.getApiKeyPrefix());
        vo.setApiKeyStatus(entity.getApiKeyStatus());
        vo.setExpireTime(entity.getExpireTime());
        vo.setLastUsedTime(entity.getLastUsedTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
