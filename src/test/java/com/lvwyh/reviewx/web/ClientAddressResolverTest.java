package com.lvwyh.reviewx.web;

import com.lvwyh.reviewx.web.security.ClientAddressResolver;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

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
    void untrustedClientCannotSpoofTheRealIpHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("198.51.100.8");
        request.addHeader("X-Real-IP", "192.168.3.91");

        assertEquals("198.51.100.8", resolver.resolve(request));
    }
}
