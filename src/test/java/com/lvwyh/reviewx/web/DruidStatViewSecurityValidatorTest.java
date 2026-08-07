package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.config.security.DruidStatViewSecurityValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Druid 管理页面必须失败关闭。 */
class DruidStatViewSecurityValidatorTest {

    @Test
    void disabledViewMayKeepCredentialsEmpty() {
        assertDoesNotThrow(() -> new DruidStatViewSecurityValidator(false, "", "").afterPropertiesSet());
    }

    @Test
    void enabledViewMustRejectEmptyCredentials() {
        assertThrows(IllegalStateException.class,
                () -> new DruidStatViewSecurityValidator(true, "", "").afterPropertiesSet());
        assertThrows(IllegalStateException.class,
                () -> new DruidStatViewSecurityValidator(true, "admin", " ").afterPropertiesSet());
    }

    @Test
    void enabledViewAcceptsConfiguredCredentials() {
        assertDoesNotThrow(() ->
                new DruidStatViewSecurityValidator(true, "reviewx-druid", "strong-password").afterPropertiesSet());
    }
}
