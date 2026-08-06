package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.config.security.AdminProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.mock.env.MockEnvironment;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 管理员环境变量对应属性的绑定回归测试。 */
class AdminPropertiesBindingTest {

    @Test
    void commaSeparatedAllowedAddressesMustBindAsAList() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("reviewx.admin.allowed-ip-addresses", "192.168.3.91,192.168.3.92");

        AdminProperties properties = Binder.get(environment)
                .bind("reviewx.admin", Bindable.of(AdminProperties.class))
                .orElseGet(AdminProperties::new);

        assertEquals(Arrays.asList("192.168.3.91", "192.168.3.92"), properties.getAllowedIpAddresses());
    }
}
