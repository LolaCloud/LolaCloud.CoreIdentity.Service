package com.miranda.luna.lci.cucumber.steps;

import com.miranda.luna.lci.bdd.CucumberSpringContextConfiguration;
import com.miranda.luna.lci.bdd.context.ScenarioContext;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OperatorProvisioningSteps {

    @Autowired
    private CucumberSpringContextConfiguration contextConfig;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private OperatorRepositoryPort operatorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @When("a request is sent to create an operator with username {string}, password {string} and permissions {string}")
    public void aRequestIsSentToCreateAnOperator(String username, String password, String permissionsStr) throws Exception {
        scenarioContext.set("lastCreatedUsername", username);

        // Clean up test non-root operator if already exists from previous runs to ensure test idempotency
        if (!"root".equalsIgnoreCase(username)) {
            operatorRepository.findByUsername(username).ifPresent(op -> operatorRepository.deleteById(op.getId()));
        }

        List<String> permissions = Arrays.stream(permissionsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, Object> payload = Map.of(
                "username", username,
                "password", password,
                "permissions", permissions
        );

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/api/v1/operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
        );

        scenarioContext.setLatestResult(result);
    }

    @Then("the operator creation response status should be {int}")
    public void theOperatorCreationResponseStatusShouldBe(int expectedStatus) throws Exception {
        scenarioContext.getLatestResult().andExpect(status().is(expectedStatus));
    }

    @Then("the created operator should have username {string}")
    public void theCreatedOperatorShouldHaveUsername(String expectedUsername) throws Exception {
        scenarioContext.getLatestResult().andExpect(jsonPath("$.data.username").value(expectedUsername));
    }

    @Then("the created operator should be listed in all operators query")
    public void theCreatedOperatorShouldBeListedInAllOperatorsQuery() throws Exception {
        String expectedUsername = scenarioContext.get("lastCreatedUsername");
        ResultActions result = contextConfig.getMockMvc().perform(
                get("/api/v1/operators")
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].username", hasItem(expectedUsername)));
    }
}
