Feature: Operator Provisioning and Management

  Scenario: Super admin provisions a new operator with granular permissions
    Given an operator exists with username "root" and password "toor"
    When a request is sent to create an operator with username "operator1", password "secret123" and permissions "relate::*, stacker::container::create"
    Then the operator creation response status should be 200
    And the created operator should have username "operator1"
    And the created operator should be listed in all operators query

  Scenario: Operator creation fails with duplicate username
    Given an operator exists with username "root" and password "toor"
    When a request is sent to create an operator with username "root", password "secret123" and permissions "relate::*"
    Then the operator creation response status should be 400
