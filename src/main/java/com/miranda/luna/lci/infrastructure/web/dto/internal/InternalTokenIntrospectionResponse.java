package com.miranda.luna.lci.infrastructure.web.dto.internal;

public record InternalTokenIntrospectionResponse(
        boolean valid,
        OperatorSummaryDto operator,
        String error
) {
    public static InternalTokenIntrospectionResponse success(OperatorSummaryDto operator) {
        return new InternalTokenIntrospectionResponse(true, operator, null);
    }

    public static InternalTokenIntrospectionResponse invalid(String reason) {
        return new InternalTokenIntrospectionResponse(false, null, reason);
    }
}
