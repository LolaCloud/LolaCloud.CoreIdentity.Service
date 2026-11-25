Feature: Operator RBAC Permissions Evaluation
  As an IAM engine
  I want to evaluate operator permission slugs in-memory
  So that access control is instantaneous and enforces least privilege

  Scenario: Root operator has unconditional bypass
    Given an operator with username "root" and no explicit permissions
    When I check permission "runway::dns::manage"
    Then permission should be granted
    When I check permission "stacker::container::restart"
    Then permission should be granted

  Scenario: Operator with global wildcard has access to all actions
    Given an operator with username "admin" and permissions "*::*::*"
    When I check permission "runway::dns::manage"
    Then permission should be granted
    When I check permission "relate::db::create"
    Then permission should be granted

  Scenario: Operator with service wildcard is restricted to that service
    Given an operator with username "netops" and permissions "runway::*"
    When I check permission "runway::dns::manage"
    Then permission should be granted
    When I check permission "runway::tunnel::create"
    Then permission should be granted
    When I check permission "stacker::container::start"
    Then permission should be denied
    When I check permission "relate::db::create"
    Then permission should be denied

  Scenario: Operator with exact permission cannot perform other actions
    Given an operator with username "devops" and permissions "stacker::container::restart"
    When I check permission "stacker::container::restart"
    Then permission should be granted
    When I check permission "stacker::container::delete"
    Then permission should be denied
