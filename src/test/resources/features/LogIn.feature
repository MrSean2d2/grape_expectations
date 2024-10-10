Feature: Log in feature
  Scenario: User is logging into their account and enters the wrong credentials
    Given the user "tester" with password "passtest" already has an account
    And is not yet logged in
    When the user enters correct username "tester",  incorrect password "pastest"
    Then error message tells the user password is correct
