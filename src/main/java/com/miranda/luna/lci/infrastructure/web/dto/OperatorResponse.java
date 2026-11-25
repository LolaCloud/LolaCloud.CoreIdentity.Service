package com.miranda.luna.lci.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public record OperatorResponse(
        UUID id,
        String username,
        List<String> permissions,
        String status
) {
    public OperatorResponse(UUID id, String username, List<String> permissions) {
        this(id, username, permissions, "ACTIVE");
    }
}
