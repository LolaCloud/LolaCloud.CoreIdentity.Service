package com.miranda.luna.lci.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 * LolaCloud Global Standard API Response Envelope.
 * Mandated across all platform microservices (LCI, Runway, Stacker, Relate, Keeper, Store).
 *
 * @param <T> Payload data type
 */
public record ApiResponse<T>(
        T data,
        @JsonProperty("request_id")
        String requestId,
        Instant timestamp
) {
    public static <T> ApiResponse<T> of(T data, String requestId) {
        return new ApiResponse<>(data, requestId, Instant.now());
    }

    public static <T> ApiResponse<T> of(T data, UUID requestId) {
        return new ApiResponse<>(data, requestId != null ? requestId.toString() : UUID.randomUUID().toString(), Instant.now());
    }
}
