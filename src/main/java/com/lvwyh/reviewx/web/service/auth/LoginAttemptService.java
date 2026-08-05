package com.lvwyh.reviewx.web.service.auth;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 登录失败限流：按账号和来源地址分别统计，降低暴力破解风险。 */
@Component
public class LoginAttemptService {

    private static final long WINDOW_MILLIS = 15L * 60L * 1000L;
    private static final int ACCOUNT_FAILURE_LIMIT = 5;
    private static final int ADDRESS_FAILURE_LIMIT = 20;
    private static final int MAX_TRACKED_KEYS = 10000;

    private final Map<String, AttemptWindow> attempts = new ConcurrentHashMap<String, AttemptWindow>();

    public void checkAllowed(String username, String address) {
        long now = System.currentTimeMillis();
        cleanupIfNeeded(now);
        check(key("account", normalize(username)), now);
        check(key("address", normalize(address)), now);
    }

    public void recordFailure(String username, String address) {
        long now = System.currentTimeMillis();
        fail(key("account", normalize(username)), ACCOUNT_FAILURE_LIMIT, now);
        fail(key("address", normalize(address)), ADDRESS_FAILURE_LIMIT, now);
    }

    public void recordSuccess(String username) {
        attempts.remove(key("account", normalize(username)));
    }

    private void check(String key, long now) {
        AttemptWindow window = attempts.get(key);
        if (window != null && window.isBlocked(now)) {
            throw new BusinessException(429, "登录失败次数过多，请稍后再试");
        }
    }

    private void fail(String key, int limit, long now) {
        AttemptWindow window = attempts.computeIfAbsent(key, ignored -> new AttemptWindow(now));
        window.recordFailure(limit, now);
    }

    private void cleanupIfNeeded(long now) {
        if (attempts.size() < MAX_TRACKED_KEYS) return;
        for (Map.Entry<String, AttemptWindow> entry : attempts.entrySet()) {
            if (entry.getValue().isExpired(now)) attempts.remove(entry.getKey(), entry.getValue());
        }
    }

    private String key(String type, String value) {
        return type + ':' + value;
    }

    private String normalize(String value) {
        return value == null ? "unknown" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static class AttemptWindow {
        private long startedAt;
        private long blockedUntil;
        private int failures;

        private AttemptWindow(long now) { this.startedAt = now; }

        private synchronized void recordFailure(int limit, long now) {
            if (now - startedAt >= WINDOW_MILLIS) {
                startedAt = now;
                failures = 0;
                blockedUntil = 0;
            }
            failures++;
            if (failures >= limit) blockedUntil = now + WINDOW_MILLIS;
        }

        private synchronized boolean isBlocked(long now) { return blockedUntil > now; }

        private synchronized boolean isExpired(long now) {
            return blockedUntil <= now && now - startedAt >= WINDOW_MILLIS;
        }
    }
}
