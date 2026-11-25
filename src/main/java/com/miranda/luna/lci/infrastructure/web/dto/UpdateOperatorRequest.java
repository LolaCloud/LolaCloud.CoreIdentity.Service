package com.miranda.luna.lci.infrastructure.web.dto;

import java.util.List;

public record UpdateOperatorRequest(
        String password,
        List<String> permissions,
        String status
) {
    public UpdateOperatorRequest(List<String> permissions) {
        this(null, permissions, null);
    }

    public UpdateOperatorRequest(String password, List<String> permissions) {
        this(password, permissions, null);
    }
}
