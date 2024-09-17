Feature: Filter feature
  Scenario: As a consumer, I want to filter out the wine into a category
    Given the user is on the base search page,
    When the user applies a year 2012 filter
    Then the system displays results of wine entries from 2012.

