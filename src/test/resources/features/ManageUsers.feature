Feature: Manage Users
  Scenario: I want to be able to grant other users admin rights
    Given a user "tester" with password "passtest1!" has registered
    When the admin grants user admin rights
    Then user now has admin role
  Scenario: As an Admin, I want to be able to delete a user's profile
    Given a user "tester" with password "passtest1!" has registered
    When the admin deletes "tester" user
    Then user "tester" with password "passtest1!" can no longer sign in