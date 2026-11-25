package com.miranda.luna.lci.infrastructure.web.dto;

public record LoginRequest(
        String username,
        String password
) {}
