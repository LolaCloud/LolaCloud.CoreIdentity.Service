package com.miranda.luna.lci.bdd.steps;

import com.miranda.luna.lci.domain.model.Operator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperatorPermissionSteps {

    private Operator currentOperator;
    private boolean lastPermissionResult;

    @Given("an operator with username {string} and no explicit permissions")
    public void anOperatorWithUsernameAndNoExplicitPermissions(String username) {
        this.currentOperator = Operator.create(username, "dummy-hash", List.of());
    }

    @Given("an operator with username {string} and permissions {string}")
    public void anOperatorWithUsernameAndPermissions(String username, String permissionsString) {
        List<String> permissions = Arrays.stream(permissionsString.split(","))
                .map(String::trim)
                .toList();
        this.currentOperator = Operator.create(username, "dummy-hash", permissions);
    }

    @When("I check permission {string}")
    public void iCheckPermission(String permission) {
        this.lastPermissionResult = currentOperator.can(permission);
    }

    @Then("permission should be granted")
    public void permissionShouldBeGranted() {
        assertTrue(lastPermissionResult, "Expected permission to be GRANTED, but it was DENIED");
    }

    @Then("permission should be denied")
    public void permissionShouldBeDenied() {
        assertFalse(lastPermissionResult, "Expected permission to be DENIED, but it was GRANTED");
    }
}
