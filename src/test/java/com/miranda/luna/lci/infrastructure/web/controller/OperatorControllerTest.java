package com.miranda.luna.lci.infrastructure.web.controller;

import com.miranda.luna.lci.application.usecase.CreateOperatorUseCase;
import com.miranda.luna.lci.application.usecase.DeleteOperatorUseCase;
import com.miranda.luna.lci.application.usecase.ListOperatorsUseCase;
import com.miranda.luna.lci.application.usecase.GetOperatorUseCase;
import com.miranda.luna.lci.application.usecase.UpdateOperatorUseCase;
import com.miranda.luna.lci.infrastructure.web.advice.GlobalExceptionHandler;
import com.miranda.luna.lci.infrastructure.web.advice.GlobalResponseWrapperAdvice;
import com.miranda.luna.lci.infrastructure.web.dto.CreateOperatorRequest;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import com.miranda.luna.lci.infrastructure.web.dto.UpdateOperatorRequest;
import com.miranda.luna.lci.infrastructure.web.filter.RequestIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OperatorControllerTest {

    private CreateOperatorUseCase createOperatorUseCase;
    private ListOperatorsUseCase listOperatorsUseCase;
    private DeleteOperatorUseCase deleteOperatorUseCase;
    private GetOperatorUseCase getOperatorUseCase;
    private UpdateOperatorUseCase updateOperatorUseCase;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        createOperatorUseCase = mock(CreateOperatorUseCase.class);
        listOperatorsUseCase = mock(ListOperatorsUseCase.class);
        deleteOperatorUseCase = mock(DeleteOperatorUseCase.class);
        getOperatorUseCase = mock(GetOperatorUseCase.class);
        updateOperatorUseCase = mock(UpdateOperatorUseCase.class);

        OperatorController controller = new OperatorController(
                createOperatorUseCase,
                listOperatorsUseCase,
                deleteOperatorUseCase,
                getOperatorUseCase,
                updateOperatorUseCase
        );

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalResponseWrapperAdvice(objectMapper), new GlobalExceptionHandler())
                .addFilters(new RequestIdFilter())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/operators should return list of operators wrapped in ApiResponse")
    void listOperatorsReturnsList() throws Exception {
        UUID id = UUID.randomUUID();
        when(listOperatorsUseCase.execute()).thenReturn(List.of(
                new OperatorResponse(id, "root", List.of("*::*::*"))
        ));

        mockMvc.perform(get("/api/v1/operators").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(id.toString()))
                .andExpect(jsonPath("$.data[0].username").value("root"))
                .andExpect(jsonPath("$.data[0].permissions[0]").value("*::*::*"))
                .andExpect(jsonPath("$.request_id").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(listOperatorsUseCase).execute();
    }

    @Test
    @DisplayName("POST /api/v1/operators should create operator and return OperatorResponse wrapped in ApiResponse")
    void createOperatorReturnsCreated() throws Exception {
        UUID id = UUID.randomUUID();
        CreateOperatorRequest request = new CreateOperatorRequest("operator1", "pass1234", List.of("relate::*"));
        OperatorResponse response = new OperatorResponse(id, "operator1", List.of("relate::*"));

        when(createOperatorUseCase.execute(any(CreateOperatorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.username").value("operator1"))
                .andExpect(jsonPath("$.data.permissions[0]").value("relate::*"))
                .andExpect(jsonPath("$.request_id").isNotEmpty());

        verify(createOperatorUseCase).execute(any(CreateOperatorRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/operators/{id} should delete operator and return confirmation message")
    void deleteOperatorReturnsSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteOperatorUseCase).execute(id);

        mockMvc.perform(delete("/api/v1/operators/" + id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("Operator deleted successfully"))
                .andExpect(jsonPath("$.request_id").isNotEmpty());

        verify(deleteOperatorUseCase).execute(id);
    }

    @Test
    @DisplayName("GET /api/v1/operators/{id} should return operator by ID or username wrapped in ApiResponse")
    void getOperatorReturnsOperator() throws Exception {
        UUID id = UUID.randomUUID();
        OperatorResponse response = new OperatorResponse(id, "operator1", List.of("relate::*"), "ACTIVE");
        when(getOperatorUseCase.execute("operator1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/operators/operator1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.username").value("operator1"))
                .andExpect(jsonPath("$.data.permissions[0]").value("relate::*"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.request_id").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(getOperatorUseCase).execute("operator1");
    }

    @Test
    @DisplayName("PUT /api/v1/operators/{id} should update operator and return OperatorResponse wrapped in ApiResponse")
    void updateOperatorReturnsUpdated() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateOperatorRequest request = new UpdateOperatorRequest("newPass123", List.of("runway::*"), "ACTIVE");
        OperatorResponse response = new OperatorResponse(id, "operator1", List.of("runway::*"), "ACTIVE");

        when(updateOperatorUseCase.execute(eq("operator1"), any(UpdateOperatorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/operators/operator1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.username").value("operator1"))
                .andExpect(jsonPath("$.data.permissions[0]").value("runway::*"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.request_id").isNotEmpty());

        verify(updateOperatorUseCase).execute(eq("operator1"), any(UpdateOperatorRequest.class));
    }
}
