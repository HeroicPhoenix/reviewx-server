package com.lvwyh.reviewx.web.config.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Druid 管理页面安全校验。
 *
 * Druid StatViewServlet 不经过 Spring MVC 的 JWT 拦截器，因此一旦启用，
 * 必须使用它自身的账号密码保护；缺少任一凭证时应用直接拒绝启动。
 */
@Component
public class DruidStatViewSecurityValidator implements InitializingBean {

    private final boolean enabled;
    private final String username;
    private final String password;

    public DruidStatViewSecurityValidator(
            @Value("${spring.datasource.druid.stat-view-servlet.enabled:false}") boolean enabled,
            @Value("${spring.datasource.druid.stat-view-servlet.login-username:}") String username,
            @Value("${spring.datasource.druid.stat-view-servlet.login-password:}") String password) {
        this.enabled = enabled;
        this.username = username;
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() {
        if (!enabled) {
            return;
        }
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalStateException(
                    "Druid StatViewServlet is enabled but login credentials are empty; "
                            + "set DRUID_LOGIN_USERNAME and DRUID_LOGIN_PASSWORD or disable DRUID_STAT_VIEW_ENABLED");
        }
    }
}
