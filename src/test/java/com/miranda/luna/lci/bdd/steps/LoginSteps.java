package com.miranda.luna.lci.bdd.steps;

import com.miranda.luna.lci.bdd.CucumberSpringContextConfiguration;
import com.miranda.luna.lci.bdd.context.ScenarioContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LoginSteps {

    @Autowired
    private CucumberSpringContextConfiguration contextConfig;

    @Autowired
    private ScenarioContext scenarioContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResultActions getLatestResult() {
        return scenarioContext.getLatestResult();
    }

    @When("I send a POST request to {string} with username {string} and password {string}")
    public void iSendAPostRequestToWithUsernameAndPassword(String endpoint, String username, String password) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        ResultActions result = contextConfig.getMockMvc().perform(
                post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the response body should contain a valid JWT token")
    public void theResponseBodyShouldContainAValidJwtToken() throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Then("the response body should contain operator username {string}")
    public void theResponseBodyShouldContainOperatorUsername(String expectedUsername) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.operator.username").value(expectedUsername));
    }

    @Then("the response body should contain permissions including {string}")
    public void theResponseBodyShouldContainPermissionsIncluding(String expectedPermission) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.operator.permissions", hasItem(expectedPermission)));
    }

    @Then("the response body should contain an error message")
    public void theResponseBodyShouldContainAnErrorMessage() throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.message").isNotEmpty());
    }
}
