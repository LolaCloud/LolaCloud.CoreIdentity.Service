package com.miranda.luna.lci.bdd.steps;

import com.miranda.luna.lci.bdd.CucumberSpringContextConfiguration;
import com.miranda.luna.lci.bdd.context.ScenarioContext;
import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OperatorEditingSteps {

    @Autowired
    private CucumberSpringContextConfiguration contextConfig;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private OperatorRepositoryPort operatorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Given("an operator exists with username {string}, password {string} and permissions {string}")
    public void anOperatorExistsWithUsernamePasswordAndPermissions(String username, String password, String permissionsStr) {
        if (!"root".equalsIgnoreCase(username)) {
            operatorRepository.findByUsername(username).ifPresent(op -> operatorRepository.deleteById(op.getId()));
        }
        List<String> permissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (!operatorRepository.existsByUsername(username)) {
            Operator operator = new Operator(
                    UUID.randomUUID(),
                    username,
                    passwordEncoder.encode(password),
                    permissions,
                    "ACTIVE",
                    Instant.now(),
                    Instant.now()
            );
            operatorRepository.save(operator);
        } else {
            Operator existing = operatorRepository.findByUsername(username).orElseThrow();
            Operator updated = new Operator(
                    existing.getId(),
                    existing.getUsername(),
                    passwordEncoder.encode(password),
                    permissions,
                    existing.getStatus(),
                    existing.getCreatedAt(),
                    Instant.now()
            );
            operatorRepository.save(updated);
        }
    }

    @When("I send a PUT request to update operator {string} with permissions {string}")
    public void iSendAPutRequestToUpdateOperatorWithPermissions(String identifier, String permissionsStr) throws Exception {
        List<String> permissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, Object> body = Map.of(
                "permissions", permissions
        );

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder =
                put("/api/v1/operators/" + identifier)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body));

        String token = scenarioContext.get("currentToken");
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        ResultActions result = contextConfig.getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @When("I send a PUT request to update operator {string} with new password {string} and permissions {string}")
    public void iSendAPutRequestToUpdateOperatorWithNewPasswordAndPermissions(String identifier, String password, String permissionsStr) throws Exception {
        List<String> permissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, Object> body = Map.of(
                "password", password,
                "permissions", permissions
        );

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder =
                put("/api/v1/operators/" + identifier)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body));

        String token = scenarioContext.get("currentToken");
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        ResultActions result = contextConfig.getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @When("I send a PUT request to update operator {string} with status {string} and permissions {string}")
    public void iSendAPutRequestToUpdateOperatorWithStatusAndPermissions(String identifier, String status, String permissionsStr) throws Exception {
        List<String> permissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, Object> body = Map.of(
                "status", status,
                "permissions", permissions
        );

        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder =
                put("/api/v1/operators/" + identifier)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body));

        String token = scenarioContext.get("currentToken");
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        ResultActions result = contextConfig.getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @When("I send a DELETE request to delete operator {string}")
    public void iSendADeleteRequestToDeleteOperator(String identifier) throws Exception {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder =
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/operators/" + identifier)
                        .accept(MediaType.APPLICATION_JSON);

        String token = scenarioContext.get("currentToken");
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        ResultActions result = contextConfig.getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @When("I retrieve operator {string} by ID or username")
    public void iRetrieveOperatorByIdOrUsername(String identifier) throws Exception {
        org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder requestBuilder =
                get("/api/v1/operators/" + identifier)
                        .accept(MediaType.APPLICATION_JSON);

        String token = scenarioContext.get("currentToken");
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        ResultActions result = contextConfig.getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @Then("the updated operator should have permissions {string}")
    public void theUpdatedOperatorShouldHavePermissions(String permissionsStr) throws Exception {
        List<String> expectedPermissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        for (String perm : expectedPermissions) {
            scenarioContext.getLatestResult().andExpect(jsonPath("$.data.permissions", hasItem(perm)));
        }
    }

    @Then("the operator should have permissions {string}")
    public void theOperatorShouldHavePermissions(String permissionsStr) throws Exception {
        theUpdatedOperatorShouldHavePermissions(permissionsStr);
    }

    @Then("operator {string} password in database should be hashed with BCrypt cost {int}")
    public void operatorPasswordInDatabaseShouldBeHashedWithBcryptCost(String username, int expectedCost) {
        Operator op = operatorRepository.findByUsername(username).orElseThrow();
        String hash = op.getPassword();
        assertTrue(hash.startsWith("$2a$" + expectedCost + "$") || hash.startsWith("$2b$" + expectedCost + "$"),
                "Password hash should start with $2a$" + expectedCost + "$ but was: " + hash);
    }
}
