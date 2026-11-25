package com.miranda.luna.lci.cucumber.steps;

import com.miranda.luna.lci.bdd.CucumberSpringContextConfiguration;
import com.miranda.luna.lci.bdd.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SessionSteps {

    @Autowired
    private CucumberSpringContextConfiguration contextConfig;

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private OperatorRepositoryPort operatorRepository;

    @Autowired
    private OperatorSessionRepositoryPort sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ResultActions getLatestResult() {
        return scenarioContext.getLatestResult();
    }

    private String buildUserAgent(String browser, String os) {
        if ("Chrome".equalsIgnoreCase(browser) && "Windows".equalsIgnoreCase(os)) {
            return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36";
        }
        if ("Safari".equalsIgnoreCase(browser) && "macOS".equalsIgnoreCase(os)) {
            return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15";
        }
        if ("Firefox".equalsIgnoreCase(browser) && "Linux".equalsIgnoreCase(os)) {
            return "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0";
        }
        return "Mozilla/5.0 (" + os + ") " + browser + "/1.0";
    }

    @When("I log in with username {string} and password {string} from {string} on {string} with IP {string}")
    public void iLogInWithDetails(String username, String password, String browser, String os, String ip) throws Exception {
        String userAgent = buildUserAgent(browser, os);
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("User-Agent", userAgent)
                        .header("X-Forwarded-For", ip)
                        .content(payload)
        );

        scenarioContext.setLatestResult(result);

        String content = result.andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        if (root.has("data") && root.get("data").has("token")) {
            String token = root.get("data").get("token").asText();
            String sessionId = root.get("data").get("sessionId").asText();
            scenarioContext.set("currentToken", token);
            scenarioContext.set("currentSessionId", sessionId);
        }
    }

    @Then("the response body should contain a valid sessionId")
    public void theResponseBodyShouldContainAValidSessionId() throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.sessionId").isString())
                .andExpect(jsonPath("$.data.sessionId").isNotEmpty());
    }

    @When("I list my active sessions")
    public void iListMyActiveSessions() throws Exception {
        String token = scenarioContext.get("currentToken");
        ResultActions result = contextConfig.getMockMvc().perform(
                get("/api/v1/sessions")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the session list should contain a session with browser {string}, os {string}, device {string}, and ip {string}")
    public void theSessionListShouldContainSession(String browser, String os, String device, String ip) throws Exception {
        String content = getLatestResult().andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode data = root.get("data");
        assertTrue(data.isArray(), "Expected data to be an array");

        boolean found = false;
        for (JsonNode session : data) {
            if (browser.equalsIgnoreCase(session.get("browser").asText()) &&
                os.equalsIgnoreCase(session.get("operatingSystem").asText()) &&
                device.equalsIgnoreCase(session.get("deviceType").asText()) &&
                ip.equals(session.get("ipAddress").asText())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Expected session matching browser=" + browser + ", os=" + os + ", device=" + device + ", ip=" + ip);
    }

    @Then("the session should be marked as current")
    public void theSessionShouldBeMarkedAsCurrent() throws Exception {
        String currentSessionId = scenarioContext.get("currentSessionId");
        String content = getLatestResult().andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode data = root.get("data");

        boolean found = false;
        for (JsonNode session : data) {
            if (currentSessionId.equals(session.get("id").asText())) {
                assertTrue(session.get("isCurrent").asBoolean(), "Expected session to have isCurrent=true");
                found = true;
                break;
            }
        }
        assertTrue(found, "Expected to find session with id " + currentSessionId);
    }

    @Given("operator {string} logs in from {string} on {string} as {string}")
    public void operatorLogsInAs(String username, String browser, String os, String sessionAlias) throws Exception {
        String userAgent = buildUserAgent(browser, os);
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", "toor"
        ));

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("User-Agent", userAgent)
                        .header("X-Forwarded-For", "10.0.0." + (sessionAlias.hashCode() % 200 + 10))
                        .content(payload)
        );

        String content = result.andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        String token = root.get("data").get("token").asText();
        String sessionId = root.get("data").get("sessionId").asText();

        scenarioContext.set(sessionAlias + "_token", token);
        scenarioContext.set(sessionAlias + "_sessionId", sessionId);
    }

    @When("I list active sessions using token from {string}")
    public void iListActiveSessionsUsingTokenFrom(String sessionAlias) throws Exception {
        String token = scenarioContext.get(sessionAlias + "_token");
        ResultActions result = contextConfig.getMockMvc().perform(
                get("/api/v1/sessions")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the active session list should contain at least {int} sessions")
    public void theActiveSessionListShouldContainAtLeastSessions(int expectedCount) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(expectedCount)));
    }

    @Then("{string} should have isCurrent {word}")
    public void sessionShouldHaveIsCurrent(String sessionAlias, String expectedBoolStr) throws Exception {
        boolean expected = Boolean.parseBoolean(expectedBoolStr);
        String targetSessionId = scenarioContext.get(sessionAlias + "_sessionId");
        assertNotNull(targetSessionId, "Target session id not found for alias: " + sessionAlias);

        String content = getLatestResult().andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode data = root.get("data");

        boolean found = false;
        for (JsonNode session : data) {
            if (targetSessionId.equals(session.get("id").asText())) {
                assertEquals(expected, session.get("isCurrent").asBoolean(),
                        "Expected isCurrent for " + sessionAlias + " to be " + expected);
                found = true;
                break;
            }
        }
        assertTrue(found, "Expected to find session in list for alias " + sessionAlias);
    }

    @When("I revoke {string} using token from {string}")
    public void iRevokeSessionUsingTokenFrom(String targetAlias, String callerAlias) throws Exception {
        String targetSessionId = scenarioContext.get(targetAlias + "_sessionId");
        String callerToken = scenarioContext.get(callerAlias + "_token");

        ResultActions result = contextConfig.getMockMvc().perform(
                delete("/api/v1/sessions/" + targetSessionId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + callerToken)
        );
        scenarioContext.setLatestResult(result);
    }

    @When("I revoke a non-existent session using token from {string}")
    public void iRevokeNonExistentSession(String callerAlias) throws Exception {
        String callerToken = scenarioContext.get(callerAlias + "_token");
        UUID randomId = UUID.randomUUID();

        ResultActions result = contextConfig.getMockMvc().perform(
                delete("/api/v1/sessions/" + randomId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + callerToken)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the response body should contain message {string}")
    public void theResponseBodyShouldContainMessage(String expectedMessage) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.message").value(expectedMessage));
    }

    @Then("{string} should not be present in active sessions")
    public void sessionShouldNotBePresentInActiveSessions(String sessionAlias) throws Exception {
        String targetSessionId = scenarioContext.get(sessionAlias + "_sessionId");
        String content = getLatestResult().andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode data = root.get("data");

        for (JsonNode session : data) {
            assertFalse(targetSessionId.equals(session.get("id").asText()),
                    "Session " + sessionAlias + " should NOT be present in active sessions");
        }
    }

    @When("I introspect token from {string}")
    public void iIntrospectTokenFrom(String sessionAlias) throws Exception {
        String token = scenarioContext.get(sessionAlias + "_token");
        String payload = objectMapper.writeValueAsString(Map.of("token", token));

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/internal/v1/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the introspection result should be invalid with reason {string}")
    public void theIntrospectionResultShouldBeInvalidWithReason(String reason) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.error").value(reason));
    }

    @Then("the response body should contain error message {string}")
    public void theResponseBodyShouldContainErrorMessage(String errorMessage) throws Exception {
        getLatestResult().andExpect(jsonPath("$.data.message").value(errorMessage));
    }

    @Given("an operator exists with username {string} and password {string}")
    public void anOperatorExistsWithUsernameAndPassword(String username, String password) {
        if (!operatorRepository.existsByUsername(username)) {
            Operator operator = Operator.create(
                    username,
                    passwordEncoder.encode(password),
                    List.of("*::*::*")
            );
            operatorRepository.save(operator);
        } else {
            Operator existing = operatorRepository.findByUsername(username).orElseThrow();
            Operator updated = new Operator(
                    existing.getId(),
                    existing.getUsername(),
                    passwordEncoder.encode(password),
                    existing.getPermissions(),
                    existing.getStatus(),
                    existing.getCreatedAt(),
                    java.time.Instant.now()
            );
            operatorRepository.save(updated);
        }
        assertTrue(operatorRepository.existsByUsername(username), "Operator " + username + " should exist");
    }

    @When("the operator logs in with username {string} and password {string}")
    public void theOperatorLogsInWithUsernameAndPassword(String username, String password) throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "username", username,
                "password", password
        ));

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload)
        );

        scenarioContext.setLatestResult(result);

        String content = result.andReturn().getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        if (root.has("data") && root.get("data").has("token")) {
            String token = root.get("data").get("token").asText();
            String sessionId = root.get("data").get("sessionId").asText();
            scenarioContext.set("currentToken", token);
            scenarioContext.set("currentSessionId", sessionId);
        }
    }

    @When("the operator logs out with their session token")
    public void theOperatorLogsOutWithTheirSessionToken() throws Exception {
        String token = scenarioContext.get("currentToken");
        ResultActions result = contextConfig.getMockMvc().perform(
                post("/api/v1/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        );
        scenarioContext.setLatestResult(result);
    }

    @Then("the logout response status should be {int}")
    public void theLogoutResponseStatusShouldBe(int expectedStatus) throws Exception {
        getLatestResult().andExpect(status().is(expectedStatus));
    }

    @Then("the session should be marked as revoked")
    public void theSessionShouldBeMarkedAsRevoked() {
        String sessionIdStr = scenarioContext.get("currentSessionId");
        assertNotNull(sessionIdStr, "Current session ID should not be null");
        UUID sessionId = UUID.fromString(sessionIdStr);
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new AssertionError("Session not found in repository: " + sessionId));
        assertTrue(session.isRevoked(), "Session " + sessionId + " should be marked as revoked");
    }

    @Then("introspecting the logged out token should report that the session is revoked")
    public void introspectingTheLoggedOutTokenShouldReportThatTheSessionIsRevoked() throws Exception {
        String token = scenarioContext.get("currentToken");
        assertNotNull(token, "Current token should not be null");
        String payload = objectMapper.writeValueAsString(Map.of("token", token));

        ResultActions result = contextConfig.getMockMvc().perform(
                post("/internal/v1/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payload)
        );
        scenarioContext.setLatestResult(result);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.error").value("Session has been revoked"));
    }
}
