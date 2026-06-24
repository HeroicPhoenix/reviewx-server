package com.lvwyh.reviewx.web.service.apikey.impl;

import com.lvwyh.reviewx.web.config.security.JwtProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * API Key 生成与 HMAC-SHA256 哈希支持类。
 */
abstract class ApiKeySecretSupport {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String API_KEY_PREFIX_HEAD = "rxk_";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final JwtProperties jwtProperties;

    protected ApiKeySecretSupport(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成完整 API Key。
     *
     * 格式：rxk_<prefix>.<secret>
     */
    protected String generateApiKey(String prefix) {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return prefix + "." + secret;
    }

    /**
     * 生成 API Key 前缀。
     */
    protected String generatePrefix() {
        byte[] randomBytes = new byte[6];
        SECURE_RANDOM.nextBytes(randomBytes);
        return API_KEY_PREFIX_HEAD + toHex(randomBytes);
    }

    /**
     * 从完整 API Key 中提取前缀。
     */
    protected String extractPrefix(String apiKey) {
        if (apiKey == null) {
            return null;
        }
        int dotIndex = apiKey.indexOf('.');
        if (dotIndex <= 0) {
            return null;
        }
        return apiKey.substring(0, dotIndex);
    }

    /**
     * 使用 HMAC-SHA256 计算完整 API Key 的哈希。
     */
    protected String hmacSha256(String apiKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return toHex(mac.doFinal(apiKey.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("API Key HMAC-SHA256 failed", e);
        }
    }

    /**
     * 常量时间比较，降低时序侧信道风险。
     */
    protected boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    private String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value & 0xff));
        }
        return builder.toString();
    }
}
