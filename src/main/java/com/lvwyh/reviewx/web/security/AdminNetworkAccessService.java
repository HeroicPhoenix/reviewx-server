package com.lvwyh.reviewx.web.security;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.config.security.AdminProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 对 ADMIN 角色执行服务端来源地址白名单校验。 */
@Component
public class AdminNetworkAccessService {

    private static final Logger log = LogManager.getLogger(AdminNetworkAccessService.class);
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    private final AdminProperties properties;

    public AdminNetworkAccessService(AdminProperties properties) {
        this.properties = properties;
    }

    public void checkAllowed(LoginUser loginUser, String clientAddress) {
        if (loginUser == null || !loginUser.getRoleCodes().contains(ADMIN_ROLE_CODE)) {
            return;
        }
        if (isAllowed(clientAddress)) {
            return;
        }
        log.warn("Rejected ADMIN access: userId={}, username={}, clientAddress={}",
                loginUser.getUserId(), loginUser.getUsername(), clientAddress);
        throw new BusinessException(403, "超级管理员仅允许从指定的局域网电脑访问");
    }

    private boolean isAllowed(String clientAddress) {
        if (!StringUtils.hasText(clientAddress) || properties.getAllowedIpAddresses().isEmpty()) {
            return false;
        }
        String normalizedClientAddress = normalize(clientAddress);
        for (String allowedAddress : properties.getAllowedIpAddresses()) {
            if (normalizedClientAddress.equals(normalize(allowedAddress))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String address) {
        if (!StringUtils.hasText(address)) {
            return "";
        }
        String normalized = address.trim();
        if (normalized.regionMatches(true, 0, "::ffff:", 0, 7)) {
            return normalized.substring(7);
        }
        return normalized;
    }
}
