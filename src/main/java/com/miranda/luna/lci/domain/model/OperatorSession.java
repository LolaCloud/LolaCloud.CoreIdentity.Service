package com.miranda.luna.lci.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure Domain Entity representing an Operator Session in Lola Core Identity (LCI).
 * Free from framework-specific dependencies.
 */
public class OperatorSession {

    private final UUID id;
    private final UUID operatorId;
    private final String ipAddress;
    private final String deviceType;
    private final String browser;
    private final String operatingSystem;
    private final String userAgent;
    private boolean revoked;
    private final Instant createdAt;
    private Instant lastActiveAt;
    private final Instant expiresAt;

    public OperatorSession(
            UUID id,
            UUID operatorId,
            String ipAddress,
            String deviceType,
            String browser,
            String operatingSystem,
            String userAgent,
            boolean revoked,
            Instant createdAt,
            Instant lastActiveAt,
            Instant expiresAt
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.operatorId = Objects.requireNonNull(operatorId, "Operator ID must not be null");
        this.ipAddress = ipAddress != null ? ipAddress : "127.0.0.1";
        this.deviceType = deviceType != null ? deviceType : "Unknown";
        this.browser = browser != null ? browser : "Unknown";
        this.operatingSystem = operatingSystem != null ? operatingSystem : "Unknown";
        this.userAgent = userAgent != null ? userAgent : "Unknown";
        this.revoked = revoked;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.lastActiveAt = lastActiveAt != null ? lastActiveAt : Instant.now();
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at must not be null");
    }

    public static OperatorSession create(
            UUID operatorId,
            String ipAddress,
            String deviceType,
            String browser,
            String operatingSystem,
            String userAgent,
            Instant expiresAt
    ) {
        Instant now = Instant.now();
        return new OperatorSession(
                UUID.randomUUID(),
                operatorId,
                ipAddress,
                deviceType,
                browser,
                operatingSystem,
                userAgent,
                false,
                now,
                now,
                expiresAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getOperatorId() {
        return operatorId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }

    public void touch() {
        this.lastActiveAt = Instant.now();
    }
}
