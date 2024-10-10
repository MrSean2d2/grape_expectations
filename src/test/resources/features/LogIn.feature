Feature: Log in feature
  Scenario: Error is displayed when incorrect password entered
    Given the user "tester" with password "passtest1!" already has an account
    And is not yet logged in
    When username "tester" with incorrect password "pastest1!" is used to log in
    Then error message tells the user password is correct
  Scenario: User logs in with correct credentials
    Given the user "tester" with password "passtest1!" already has an account
    And is not yet logged in
    When username "tester" with password "passtest1!" is used to log in
    Then the user is logged in and taken to account page
  Scenario: Error is displayed when incorrect username entered
    Given the user "tester" with password "passtest1!" already has an account
    And is not yet logged in
    When incorrect username "test" with password "passtest1!" is used to log in
    Then error message tells the user username is wrong
