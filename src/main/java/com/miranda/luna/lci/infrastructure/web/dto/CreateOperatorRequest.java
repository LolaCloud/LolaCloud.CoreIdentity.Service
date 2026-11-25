package com.miranda.luna.lci.infrastructure.web.dto;

import java.util.List;

public record CreateOperatorRequest(
        String username,
        String password,
        List<String> permissions
) {}
