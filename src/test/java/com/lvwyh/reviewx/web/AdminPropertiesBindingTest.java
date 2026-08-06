package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.config.security.AdminProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 管理员环境变量对应属性的绑定回归测试。 */
class AdminPropertiesBindingTest {

    @Test
    void loginEnabledMustDefaultToTrueAndBindFalse() {
        assertTrue(new AdminProperties().isLoginEnabled());

        MockEnvironment environment = new MockEnvironment()
                .withProperty("reviewx.admin.login-enabled", "false");

        AdminProperties properties = Binder.get(environment)
                .bind("reviewx.admin", Bindable.of(AdminProperties.class))
                .orElseGet(AdminProperties::new);

        assertFalse(properties.isLoginEnabled());
    }
}
