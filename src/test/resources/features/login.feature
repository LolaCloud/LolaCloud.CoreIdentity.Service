Feature: Operator Authentication & JWT Token Issuance
  As a LolaCloud operator
  I want to authenticate with my credentials
  So that I receive a signed JWT token and access the platform

  Scenario: Operator successfully logs in with valid credentials
    When I send a POST request to "/api/v1/auth/login" with username "root" and password "toor"
    Then the response status code should be 200
    And the response body should contain a valid JWT token
    And the response body should contain operator username "root"
    And the response body should contain permissions including "*::*::*"
    And the response body should contain a valid request_id

  Scenario: Operator login fails with invalid password
    When I send a POST request to "/api/v1/auth/login" with username "root" and password "wrong-password"
    Then the response status code should be 401
    And the response body should contain an error message

  Scenario: Operator login fails with non-existent username
    When I send a POST request to "/api/v1/auth/login" with username "ghost_user" and password "any-password"
    Then the response status code should be 401
    And the response body should contain an error message
