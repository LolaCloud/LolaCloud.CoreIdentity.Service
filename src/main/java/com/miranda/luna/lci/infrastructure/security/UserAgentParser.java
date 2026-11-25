package com.miranda.luna.lci.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UserAgentParser {

    public record ClientInfo(
            String ipAddress,
            String deviceType,
            String browser,
            String operatingSystem,
            String userAgent
    ) {}

    public ClientInfo parse(HttpServletRequest request) {
        String ip = extractClientIp(request);
        String ua = request != null ? request.getHeader("User-Agent") : null;
        return parse(ip, ua);
    }

    public ClientInfo parse(String ipAddress, String userAgent) {
        String safeIp = (ipAddress != null && !ipAddress.isBlank()) ? ipAddress.trim() : "127.0.0.1";
        String safeUa = (userAgent != null) ? userAgent.trim() : "";
        return new ClientInfo(
                safeIp,
                extractDeviceType(safeUa),
                extractBrowser(safeUa),
                extractOperatingSystem(safeUa),
                safeUa.isEmpty() ? "Unknown" : safeUa
        );
    }

    public static String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            String firstIp = xForwardedFor.split(",")[0].trim();
            if (!firstIp.isEmpty()) {
                return firstIp;
            }
        }
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null && !remoteAddr.isBlank()) {
            return remoteAddr.trim();
        }
        return "127.0.0.1";
    }

    public static String extractOperatingSystem(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String uaLower = userAgent.toLowerCase();

        if (uaLower.contains("android")) {
            return "Android";
        }
        if (uaLower.contains("iphone") || uaLower.contains("ipad") || uaLower.contains("ipod") || uaLower.contains("ios")) {
            return "iOS";
        }
        if (uaLower.contains("mac os") || uaLower.contains("macintosh") || uaLower.contains("macos")) {
            return "macOS";
        }
        if (uaLower.contains("windows") || uaLower.contains("win32") || uaLower.contains("win64")) {
            return "Windows";
        }
        if (uaLower.contains("linux") || uaLower.contains("x11")) {
            return "Linux";
        }
        return "Unknown";
    }

    public static String extractBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String uaLower = userAgent.toLowerCase();

        if (uaLower.contains("postman") || uaLower.contains("curl")) {
            return "Postman/Curl";
        }
        if (uaLower.contains("edg/") || uaLower.contains("edge/")) {
            return "Edge";
        }
        if (uaLower.contains("opr/") || uaLower.contains("opera")) {
            return "Opera";
        }
        // Chrome check must precede Safari check because Chrome user agents contain "Safari"
        if (uaLower.contains("chrome") || uaLower.contains("crios")) {
            return "Chrome";
        }
        if (uaLower.contains("safari")) {
            return "Safari";
        }
        if (uaLower.contains("firefox") || uaLower.contains("fxios")) {
            return "Firefox";
        }
        return "Unknown";
    }

    public static String extractDeviceType(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String uaLower = userAgent.toLowerCase();

        if (uaLower.contains("ipad") || uaLower.contains("tablet")) {
            return "Tablet";
        }
        if (uaLower.contains("android")) {
            if (uaLower.contains("mobile")) {
                return "Mobile";
            }
            return "Tablet";
        }
        if (uaLower.contains("iphone") || uaLower.contains("ipod") || uaLower.contains("mobile")) {
            return "Mobile";
        }
        String os = extractOperatingSystem(userAgent);
        if ("Windows".equals(os) || "macOS".equals(os) || "Linux".equals(os)) {
            return "Desktop";
        }
        if (uaLower.contains("postman") || uaLower.contains("curl")) {
            return "Desktop";
        }
        return "Unknown";
    }
}
