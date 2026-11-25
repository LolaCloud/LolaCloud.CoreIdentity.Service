package com.miranda.luna.lci.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID id,
        String deviceType,
        String browser,
        String operatingSystem,
        String ipAddress,
        Instant createdAt,
        Instant lastActiveAt,
        Instant expiresAt,
        @JsonProperty("isCurrent")
        boolean isCurrent
) {}
