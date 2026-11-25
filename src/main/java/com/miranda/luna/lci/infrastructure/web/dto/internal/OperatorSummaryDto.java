package com.miranda.luna.lci.infrastructure.web.dto.internal;

import java.util.List;
import java.util.UUID;

public record OperatorSummaryDto(
        UUID id,
        String username,
        List<String> permissions
) {}
