Feature: Platform Health & Observability
  As a client or monitoring system
  I want to query the health endpoint
  So that I can verify service availability and receive a correlated request ID

  Scenario: Health check returns status UP in standard envelope
    When I send a GET request to "/health"
    Then the response status code should be 200
    And the response header "X-Request-ID" should be present
    And the response body should contain data status "UP"
    And the response body should contain a valid request_id
    And the response body should contain a valid timestamp

  Scenario: Health check preserves client-supplied X-Request-ID
    Given the request has header "X-Request-ID" set to "trace-client-abc-123"
    When I send a GET request to "/health"
    Then the response status code should be 200
    And the response header "X-Request-ID" should be "trace-client-abc-123"
    And the response body request_id should be "trace-client-abc-123"
