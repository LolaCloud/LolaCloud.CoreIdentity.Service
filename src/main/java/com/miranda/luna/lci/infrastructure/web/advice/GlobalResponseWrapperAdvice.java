package com.miranda.luna.lci.infrastructure.web.advice;

import com.miranda.luna.lci.infrastructure.web.dto.ApiResponse;
import com.miranda.luna.lci.infrastructure.web.filter.RequestIdFilter;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestControllerAdvice
public class GlobalResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public GlobalResponseWrapperAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Exclude raw byte arrays or streaming outputs if present
        Class<?> paramType = returnType.getParameterType();
        return !byte[].class.isAssignableFrom(paramType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        // Do not double wrap if already wrapped
        if (body instanceof ApiResponse<?>) {
            return body;
        }

        String requestId = null;
        if (request instanceof ServletServerHttpRequest servletRequest) {
            Object reqIdAttr = servletRequest.getServletRequest().getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
            if (reqIdAttr != null) {
                requestId = reqIdAttr.toString();
            }
        }

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ApiResponse<Object> apiResponse = ApiResponse.of(body, requestId);

        // String responses require manual serialization to prevent StringHttpMessageConverter ClassCastException
        if (body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(apiResponse);
            } catch (Exception e) {
                return apiResponse;
            }
        }

        return apiResponse;
    }
}
