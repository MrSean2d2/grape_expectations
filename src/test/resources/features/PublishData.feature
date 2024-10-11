Feature: Publish Data
  Scenario: As a vineyard owner, I want to publish data of a wine
    Given vineyard "test vineyard" in "test region"
    When the vineyard owner enters wine with details "test p wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard
    Then the data is saved, and the record is added
  Scenario: As an admin, I want to be notified if I enter a record that already exists in the database
    Given vineyard "test vineyard" in "test region"
    And the vineyard owner enters wine with details "test p wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard
    When attempts to add another wine with same details "test p wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard
    Then the system detects wine with same name in database and rejects duplicate