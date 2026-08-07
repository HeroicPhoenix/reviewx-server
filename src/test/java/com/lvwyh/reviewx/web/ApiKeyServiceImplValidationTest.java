package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.ao.apikey.ApiKeyCreateAO;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.JwtProperties;
import com.lvwyh.reviewx.web.mapper.apikey.SysUserApiKeyMapper;
import com.lvwyh.reviewx.web.service.apikey.impl.ApiKeyServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

/** API Key 创建参数的服务层防御性校验。 */
class ApiKeyServiceImplValidationTest {

    @Test
    void pastExpirationMustBeRejectedBeforeGeneratingOrWritingKey() {
        SysUserApiKeyMapper mapper = mock(SysUserApiKeyMapper.class);
        ApiKeyServiceImpl service = new ApiKeyServiceImpl(mapper, new JwtProperties());
        ApiKeyCreateAO ao = new ApiKeyCreateAO();
        ao.setApiKeyName("expired-key");
        ao.setExpireTime(LocalDateTime.now().minusMinutes(1));

        BusinessException error = assertThrows(BusinessException.class, () -> service.create(1L, ao));

        assertEquals(400, error.getCode());
        verifyNoInteractions(mapper);
    }
}
