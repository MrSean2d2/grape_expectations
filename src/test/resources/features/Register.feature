Feature: Register feature
  Scenario: Registering a new user
    Given the user "tester" does not already have an account
    And is not logged in on the account page
    When username "tester" with password "passtest1!" is registered
    Then the user is taken to account page
  Scenario: User enters username that is already taken
    Given user "tester" already exists
    When another user attempts to register using same username "tester"
    Then error message tells the user account with that username already exists
  Scenario: User selects a username that is less than 4 characters
    Given the user "tes" does not already have an account
    And is not logged in on the account page
    When username "tes" with password "passtest1!" is registered
    Then error message tells the user username must be between four and twenty characters
  Scenario: User enters a password that does not match what is required
    Given the user "tester" does not already have an account
    And is not logged in on the account page
    When username "tester" with password "passtest1" is registered
    Then error message tells the user password must contain a special character
  Scenario: User tries to proceed without a username
    Given the user "tester" does not already have an account
    And is not logged in on the account page
    When username "" with password "passtest1!" is registered
    Then error message tells the user username cannot be empty
  Scenario: User does not enter a password
    Given the user "tester" does not already have an account
    And is not logged in on the account page
    When username "tester" with password "" is registered
    Then error message tells the user password cannot be empty