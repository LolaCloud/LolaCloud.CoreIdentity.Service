package com.miranda.luna.lci.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAgentParserTest {

    private UserAgentParser parser;

    @BeforeEach
    void setUp() {
        parser = new UserAgentParser();
    }

    @Test
    void parseChromeOnWindows() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        UserAgentParser.ClientInfo info = parser.parse("192.168.1.10", ua);

        assertEquals("Chrome", info.browser());
        assertEquals("Windows", info.operatingSystem());
        assertEquals("Desktop", info.deviceType());
        assertEquals("192.168.1.10", info.ipAddress());
    }

    @Test
    void parseFirefoxOnLinux() {
        String ua = "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.5", ua);

        assertEquals("Firefox", info.browser());
        assertEquals("Linux", info.operatingSystem());
        assertEquals("Desktop", info.deviceType());
    }

    @Test
    void parseSafariOnMacOS() {
        String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.6", ua);

        assertEquals("Safari", info.browser());
        assertEquals("macOS", info.operatingSystem());
        assertEquals("Desktop", info.deviceType());
    }

    @Test
    void parseEdgeOnWindows() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.7", ua);

        assertEquals("Edge", info.browser());
        assertEquals("Windows", info.operatingSystem());
        assertEquals("Desktop", info.deviceType());
    }

    @Test
    void parseOpera() {
        String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 OPR/106.0.0.0";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.8", ua);

        assertEquals("Opera", info.browser());
        assertEquals("Windows", info.operatingSystem());
        assertEquals("Desktop", info.deviceType());
    }

    @Test
    void parsePostmanAndCurl() {
        UserAgentParser.ClientInfo postman = parser.parse("127.0.0.1", "PostmanRuntime/7.39.0");
        assertEquals("Postman/Curl", postman.browser());
        assertEquals("Desktop", postman.deviceType());

        UserAgentParser.ClientInfo curl = parser.parse("127.0.0.1", "curl/8.5.0");
        assertEquals("Postman/Curl", curl.browser());
        assertEquals("Desktop", curl.deviceType());
    }

    @Test
    void parseMobileIPhone() {
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.9", ua);

        assertEquals("Safari", info.browser());
        assertEquals("iOS", info.operatingSystem());
        assertEquals("Mobile", info.deviceType());
    }

    @Test
    void parseTabletIPad() {
        String ua = "Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.10", ua);

        assertEquals("Safari", info.browser());
        assertEquals("iOS", info.operatingSystem());
        assertEquals("Tablet", info.deviceType());
    }

    @Test
    void parseAndroidMobile() {
        String ua = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.11", ua);

        assertEquals("Chrome", info.browser());
        assertEquals("Android", info.operatingSystem());
        assertEquals("Mobile", info.deviceType());
    }

    @Test
    void parseAndroidTablet() {
        String ua = "Mozilla/5.0 (Linux; Android 14; SM-X910) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        UserAgentParser.ClientInfo info = parser.parse("10.0.0.12", ua);

        assertEquals("Chrome", info.browser());
        assertEquals("Android", info.operatingSystem());
        assertEquals("Tablet", info.deviceType());
    }

    @Test
    void parseNullAndUnknown() {
        UserAgentParser.ClientInfo info = parser.parse((String) null, null);

        assertEquals("Unknown", info.browser());
        assertEquals("Unknown", info.operatingSystem());
        assertEquals("Unknown", info.deviceType());
        assertEquals("127.0.0.1", info.ipAddress());
    }

    @Test
    void extractClientIpFromXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178");
        request.setRemoteAddr("10.0.0.1");

        String ip = parser.extractClientIp(request);
        assertEquals("203.0.113.195", ip);
    }

    @Test
    void extractClientIpFallbackToRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");

        String ip = parser.extractClientIp(request);
        assertEquals("192.168.1.100", ip);
    }
}
