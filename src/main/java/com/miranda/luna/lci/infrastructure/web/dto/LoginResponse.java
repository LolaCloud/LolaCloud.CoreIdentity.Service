package com.miranda.luna.lci.infrastructure.web.dto;

import java.util.UUID;

public record LoginResponse(
        String token,
        OperatorResponse operator,
        UUID sessionId
) {
    public LoginResponse(String token, OperatorResponse operator) {
        this(token, operator, null);
    }
}
