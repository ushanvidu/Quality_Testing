Feature: User Management
  As a system administrator
  I want to manage users in the system
  So that I can maintain accurate user records

  Scenario: Add a new user successfully
    Given I have a user with name "John Doe", email "john@example.com", and age 30
    When I add the user to the system
    Then the user should be saved successfully
    And the response should contain the user details

  Scenario: Fail to add user with duplicate email
    Given a user with email "john@example.com" already exists in the system
    When I try to add another user with email "john@example.com"
    Then the system should return a duplicate email error

  Scenario: Delete an existing user successfully
    Given a user with ID exists in the system
    When I delete the user
    Then the user should be removed from the system

  Scenario: Fail to delete non-existent user
    Given no user with ID 999 exists in the system
    When I try to delete user with ID 999
    Then the system should return a user not found error