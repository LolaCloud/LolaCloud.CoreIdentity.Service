package com.miranda.luna.lci.infrastructure.web.controller;

import com.miranda.luna.lci.application.usecase.CreateOperatorUseCase;
import com.miranda.luna.lci.application.usecase.DeleteOperatorUseCase;
import com.miranda.luna.lci.application.usecase.ListOperatorsUseCase;
import com.miranda.luna.lci.application.usecase.GetOperatorUseCase;
import com.miranda.luna.lci.application.usecase.UpdateOperatorUseCase;
import com.miranda.luna.lci.infrastructure.web.dto.CreateOperatorRequest;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import com.miranda.luna.lci.infrastructure.web.dto.UpdateOperatorRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operators")
public class OperatorController {

    private final CreateOperatorUseCase createOperatorUseCase;
    private final ListOperatorsUseCase listOperatorsUseCase;
    private final DeleteOperatorUseCase deleteOperatorUseCase;
    private final GetOperatorUseCase getOperatorUseCase;
    private final UpdateOperatorUseCase updateOperatorUseCase;
    private final com.miranda.luna.lci.infrastructure.security.JwtService jwtService;

    @org.springframework.beans.factory.annotation.Autowired
    public OperatorController(
            CreateOperatorUseCase createOperatorUseCase,
            ListOperatorsUseCase listOperatorsUseCase,
            DeleteOperatorUseCase deleteOperatorUseCase,
            GetOperatorUseCase getOperatorUseCase,
            UpdateOperatorUseCase updateOperatorUseCase,
            @org.springframework.beans.factory.annotation.Autowired(required = false)
            com.miranda.luna.lci.infrastructure.security.JwtService jwtService
    ) {
        this.createOperatorUseCase = createOperatorUseCase;
        this.listOperatorsUseCase = listOperatorsUseCase;
        this.deleteOperatorUseCase = deleteOperatorUseCase;
        this.getOperatorUseCase = getOperatorUseCase;
        this.updateOperatorUseCase = updateOperatorUseCase;
        this.jwtService = jwtService;
    }

    public OperatorController(
            CreateOperatorUseCase createOperatorUseCase,
            ListOperatorsUseCase listOperatorsUseCase,
            DeleteOperatorUseCase deleteOperatorUseCase,
            GetOperatorUseCase getOperatorUseCase,
            UpdateOperatorUseCase updateOperatorUseCase
    ) {
        this(createOperatorUseCase, listOperatorsUseCase, deleteOperatorUseCase, getOperatorUseCase, updateOperatorUseCase, null);
    }

    @GetMapping
    public ResponseEntity<List<OperatorResponse>> listOperators() {
        return ResponseEntity.ok(listOperatorsUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperatorResponse> getOperator(@PathVariable("id") String id) {
        return ResponseEntity.ok(getOperatorUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<OperatorResponse> createOperator(@RequestBody CreateOperatorRequest request) {
        OperatorResponse response = createOperatorUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperatorResponse> updateOperator(
            @PathVariable("id") String id,
            @RequestBody UpdateOperatorRequest request,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Authorization", required = false) String authHeader,
            org.springframework.security.core.Authentication authentication
    ) {
        String callerUsername = extractCallerUsername(authHeader, authentication);
        OperatorResponse response;
        if (callerUsername != null) {
            response = updateOperatorUseCase.execute(id, request, callerUsername);
        } else {
            response = updateOperatorUseCase.execute(id, request);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOperator(@PathVariable("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            deleteOperatorUseCase.execute(uuid);
        } catch (IllegalArgumentException e) {
            deleteOperatorUseCase.execute(id);
        }
        return ResponseEntity.ok(Map.of("message", "Operator deleted successfully"));
    }

    private String extractCallerUsername(String authHeader, org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }

        org.springframework.security.core.Authentication secAuth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (secAuth != null && secAuth.isAuthenticated() && !"anonymousUser".equals(secAuth.getPrincipal())) {
            return secAuth.getName();
        }

        if (authHeader != null && authHeader.startsWith("Bearer ") && jwtService != null) {
            String token = authHeader.substring(7).trim();
            if (!token.isEmpty()) {
                try {
                    return jwtService.extractUsername(token);
                } catch (Exception ignored) {
                }
            }
        }

        return null;
    }
}
