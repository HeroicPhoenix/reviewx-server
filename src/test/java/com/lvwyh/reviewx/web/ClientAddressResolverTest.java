package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.config.security.AdminProperties;
import com.lvwyh.reviewx.web.security.AdminNetworkAccessService;
import com.lvwyh.reviewx.web.security.ClientAddressResolver;
import com.lvwyh.reviewx.web.security.LoginUser;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** 客户端地址解析回归测试。 */
class ClientAddressResolverTest {

    private final ClientAddressResolver resolver = new ClientAddressResolver();

    @Test
    void directLanRequestMustUseTheComputerAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.3.91");

        assertEquals("192.168.3.91", resolver.resolve(request));
    }

    @Test
    void trustedDockerProxyMaySupplyTheObservedClientAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("172.17.0.5");
        request.addHeader("X-Real-IP", "192.168.3.91");

        assertEquals("192.168.3.91", resolver.resolve(request));
    }

    @Test
    void deployedSynologyFrontProxyMaySupplyTheLanComputerAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.32.3");
        request.addHeader("X-Real-IP", "192.168.3.91");

        assertEquals("192.168.3.91", resolver.resolve(request));
    }

    @Test
    void anotherSynologyDockerContainerCannotSpoofTheLanComputerAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.32.4");
        request.addHeader("X-Real-IP", "192.168.3.91");

        assertEquals("192.168.32.4", resolver.resolve(request));
    }

    @Test
    void untrustedClientCannotSpoofTheRealIpHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("198.51.100.8");
        request.addHeader("X-Real-IP", "192.168.3.91");

        assertEquals("198.51.100.8", resolver.resolve(request));
    }

    @Test
    void configuredComputerAddressMustPassTheAdminWhitelist() {
        AdminProperties properties = new AdminProperties();
        properties.setAllowedIpAddresses(Collections.singletonList("192.168.3.91"));
        AdminNetworkAccessService accessService = new AdminNetworkAccessService(properties);
        LoginUser admin = new LoginUser();
        admin.setUserId(1L);
        admin.setUsername("admin");
        admin.setRoleCodes(Collections.singleton("ADMIN"));

        assertDoesNotThrow(() -> accessService.checkAllowed(admin, "192.168.3.91"));
    }
}
