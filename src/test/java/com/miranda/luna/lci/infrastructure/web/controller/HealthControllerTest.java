package com.miranda.luna.lci.infrastructure.web.controller;

import com.miranda.luna.lci.infrastructure.web.advice.GlobalResponseWrapperAdvice;
import com.miranda.luna.lci.infrastructure.web.filter.RequestIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new HealthController())
                .setControllerAdvice(new GlobalResponseWrapperAdvice(objectMapper))
                .addFilters(new RequestIdFilter())
                .build();
    }

    @Test
    @DisplayName("GET /health should return standard envelope with data, request_id and timestamp")
    void healthReturnsStandardEnvelope() throws Exception {
        mockMvc.perform(get("/health").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(RequestIdFilter.REQUEST_ID_HEADER))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.request_id").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("GET /health with existing X-Request-ID header should preserve it")
    void healthPreservesCustomRequestId() throws Exception {
        String customRequestId = "test-req-12345";

        mockMvc.perform(get("/health")
                        .header(RequestIdFilter.REQUEST_ID_HEADER, customRequestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(RequestIdFilter.REQUEST_ID_HEADER, customRequestId))
                .andExpect(jsonPath("$.request_id").value(customRequestId))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }
}
