package com.lvwyh.reviewx.web.security;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 解析客户端来源地址。
 *
 * 仅当请求来自本机或 Docker 常用私有网段代理时才信任代理头，避免局域网客户端
 * 直接访问后端并伪造 X-Real-IP 绕过管理员地址限制。
 */
@Component
public class ClientAddressResolver {

    public String resolve(HttpServletRequest request) {
        String remoteAddress = normalize(request.getRemoteAddr());
        if (!isTrustedInternalProxy(remoteAddress)) {
            return remoteAddress;
        }

        String realIp = normalize(request.getHeader("X-Real-IP"));
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }

        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            String[] addresses = forwarded.split(",");
            return normalize(addresses[addresses.length - 1]);
        }
        return remoteAddress;
    }

    private boolean isTrustedInternalProxy(String address) {
        if (!StringUtils.hasText(address)) {
            return false;
        }
        if ("127.0.0.1".equals(address) || "::1".equals(address)) {
            return true;
        }

        int[] ipv4 = parseIpv4(address);
        if (ipv4 == null) {
            return false;
        }
        if (ipv4[0] == 10) {
            return true;
        }
        return ipv4[0] == 172 && ipv4[1] >= 16 && ipv4[1] <= 31;
    }

    private int[] parseIpv4(String address) {
        String[] parts = address.split("\\.");
        if (parts.length != 4) {
            return null;
        }
        int[] result = new int[4];
        try {
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i]);
                if (result[i] < 0 || result[i] > 255) {
                    return null;
                }
            }
            return result;
        } catch (NumberFormatException ignored) {
            return null;
        }
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
