Feature: Operator Editing
  As a LolaCloud administrator
  I want to update operator details including permissions, passwords, and status
  So that operator privileges and access remain secure and accurate

  Scenario: Successfully update operator permissions
    Given an operator exists with username "operator_edit_perm", password "securePass123" and permissions "runway::dns::manage"
    When I send a PUT request to update operator "operator_edit_perm" with permissions "runway::dns::manage, relate::db::read"
    Then the response status code should be 200
    And the updated operator should have permissions "runway::dns::manage, relate::db::read"
    And the response body should contain a valid request_id
    And the response body should contain a valid timestamp
    When I retrieve operator "operator_edit_perm" by ID or username
    Then the response status code should be 200
    And the operator should have permissions "runway::dns::manage, relate::db::read"

  Scenario: Successfully update operator password
    Given an operator exists with username "operator_edit_pass", password "initialPass123" and permissions "runway::*"
    When I send a PUT request to update operator "operator_edit_pass" with new password "updatedPass456" and permissions "runway::*"
    Then the response status code should be 200
    And operator "operator_edit_pass" password in database should be hashed with BCrypt cost 12
    When I send a POST request to "/api/v1/auth/login" with username "operator_edit_pass" and password "updatedPass456"
    Then the response status code should be 200
    And the response body should contain a valid JWT token
    When I send a POST request to "/api/v1/auth/login" with username "operator_edit_pass" and password "initialPass123"
    Then the response status code should be 401
    And the response body should contain an error message

  Scenario: Protect root operator from having global wildcard removed
    Given an operator exists with username "root" and password "toor"
    When the operator logs in with username "root" and password "toor"
    And I send a PUT request to update operator "root" with permissions "runway::dns::manage"
    Then the response status code should be 400
    And the response body should contain error message "Root operator must retain global wildcard permission (*::*::*)"

  Scenario: Non-root operator cannot edit root account
    Given an operator exists with username "root" and password "toor"
    And an operator exists with username "admin_editor", password "adminPass123" and permissions "*::*::*"
    When the operator logs in with username "admin_editor" and password "adminPass123"
    And I send a PUT request to update operator "root" with permissions "*::*::*"
    Then the response status code should be 403
    And the response body should contain error message "Only the root operator can modify the root account"

  Scenario: Root operator can edit root account
    Given an operator exists with username "root" and password "toor"
    When the operator logs in with username "root" and password "toor"
    And I send a PUT request to update operator "root" with new password "newRootPass123" and permissions "*::*::*, runway::*"
    Then the response status code should be 200
    And the updated operator should have permissions "*::*::*, runway::*"
    When the operator logs in with username "root" and password "newRootPass123"
    Then the response status code should be 200
    And the response body should contain a valid JWT token
    When I send a PUT request to update operator "root" with new password "toor" and permissions "*::*::*"
    Then the response status code should be 200

  Scenario: Deleting root operator is prohibited
    Given an operator exists with username "root" and password "toor"
    When I send a DELETE request to delete operator "root"
    Then the response status code should be 400
    And the response body should contain error message "Cannot delete the root super administrator"

  Scenario: Updating non-existent operator returns 404 Not Found
    When I send a PUT request to update operator "non_existent_operator" with permissions "runway::*"
    Then the response status code should be 404
    And the response body should contain error message "Operator not found: non_existent_operator"
    When I retrieve operator "non_existent_operator" by ID or username
    Then the response status code should be 404
    And the response body should contain error message "Operator not found: non_existent_operator"
