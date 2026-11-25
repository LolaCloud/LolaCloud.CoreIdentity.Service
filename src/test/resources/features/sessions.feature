Feature: Operator Session Management
  As an authenticated LolaCloud operator
  I want to manage my active login sessions
  So that I can monitor access and revoke untrusted devices

  Scenario: Successful login records a new session with device and browser
    When I log in with username "root" and password "toor" from "Chrome" on "Windows" with IP "192.168.1.50"
    Then the response status code should be 200
    And the response body should contain a valid sessionId
    When I list my active sessions
    Then the response status code should be 200
    And the session list should contain a session with browser "Chrome", os "Windows", device "Desktop", and ip "192.168.1.50"
    And the session should be marked as current

  Scenario: Operator can list active sessions and the current session has isCurrent = true
    Given operator "root" logs in from "Safari" on "macOS" as "session A"
    And operator "root" logs in from "Firefox" on "Linux" as "session B"
    When I list active sessions using token from "session B"
    Then the response status code should be 200
    And the active session list should contain at least 2 sessions
    And "session B" should have isCurrent true
    And "session A" should have isCurrent false

  Scenario: Operator can revoke another active session
    Given operator "root" logs in from "Safari" on "macOS" as "session A"
    And operator "root" logs in from "Firefox" on "Linux" as "session B"
    When I revoke "session A" using token from "session B"
    Then the response status code should be 200
    And the response body should contain message "Session revoked successfully"
    When I list active sessions using token from "session B"
    Then "session A" should not be present in active sessions
    When I introspect token from "session A"
    Then the introspection result should be invalid with reason "Session has been revoked"

  Scenario: Operator cannot revoke their own current session (returns 400 Bad Request)
    Given operator "root" logs in from "Chrome" on "Windows" as "current session"
    When I revoke "current session" using token from "current session"
    Then the response status code should be 400
    And the response body should contain error message "Cannot disconnect the current active session. Use sign out instead."

  Scenario: Operator cannot revoke a non-existent session
    Given operator "root" logs in from "Chrome" on "Windows" as "active session"
    When I revoke a non-existent session using token from "active session"
    Then the response status code should be 404
    And the response body should contain error message "Session not found"

  Scenario: Logging out revokes the operator session
    Given an operator exists with username "root" and password "toor"
    When the operator logs in with username "root" and password "toor"
    And the operator logs out with their session token
    Then the logout response status should be 200
    And the session should be marked as revoked
    And introspecting the logged out token should report that the session is revoked

