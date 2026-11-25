package com.miranda.luna.lci.bdd.steps;

import com.miranda.luna.lci.bdd.CucumberSpringContextConfiguration;
import com.miranda.luna.lci.bdd.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthSteps {

    @Autowired
    private CucumberSpringContextConfiguration contextConfig;

    @Autowired
    private ScenarioContext scenarioContext;

    private MockMvc mockMvc;
    private final Map<String, String> headers = new HashMap<>();

    private MockMvc getMockMvc() {
        if (mockMvc == null) {
            mockMvc = contextConfig.getMockMvc();
        }
        return mockMvc;
    }

    private ResultActions getLatestResult() {
        return scenarioContext.getLatestResult();
    }

    @Given("the request has header {string} set to {string}")
    public void theRequestHasHeaderSetTo(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String path) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(path)
                .accept(MediaType.APPLICATION_JSON);

        headers.forEach(requestBuilder::header);
        ResultActions result = getMockMvc().perform(requestBuilder);
        scenarioContext.setLatestResult(result);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatus) throws Exception {
        getLatestResult().andExpect(status().is(expectedStatus));
    }

    @Then("the response header {string} should be present")
    public void theResponseHeaderShouldBePresent(String headerName) throws Exception {
        getLatestResult().andExpect(header().exists(headerName));
    }

    @Then("the response header {string} should be {string}")
    public void theResponseHeaderShouldBe(String headerName, String expectedValue) throws Exception {
        getLatestResult().andExpect(header().string(headerName, expectedValue));
    }

    @Then("the response body should contain data status {string}")
    public void theResponseBodyShouldContainDataStatus(String expectedStatus) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.status").value(expectedStatus));
    }

    @Then("the response body should contain a valid request_id")
    public void theResponseBodyShouldContainAValidRequestId() throws Exception {
        getLatestResult().andExpect(jsonPath("$.request_id").isNotEmpty());
    }

    @Then("the response body should contain a valid timestamp")
    public void theResponseBodyShouldContainAValidTimestamp() throws Exception {
        getLatestResult().andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Then("the response body request_id should be {string}")
    public void theResponseBodyRequestIdShouldBe(String expectedRequestId) throws Exception {
        getLatestResult().andExpect(jsonPath("$.request_id").value(expectedRequestId));
    }
}
