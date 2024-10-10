Feature: Edit Wine Data
  Scenario: As an admin, I want to be able to edit existing data records
    Given a vineyard "test vineyard" in "test region"
    And wine with details "test e wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard in database
    When the admin changes the description of the wine to "very nice wine"
    Then the system updates the existing record with the new data
  Scenario: As an admin, I want to be unable to save incorrect data (year) to the database
    Given a vineyard "test vineyard" in "test region"
    And wine with details "test e wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard in database
    When the admin enters the year 0 and submits the form
    Then data is not saved, error message of year can't be in the future or older than 1700
  Scenario: As an admin, I want to be unable to save incorrect data (price) to the database
    Given a vineyard "test vineyard" in "test region"
    And wine with details "test e wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard in database
    When the admin enters the price -1 and submits the form
    Then data is not saved, error message price can't be negative
  Scenario: As an admin, I want to be unable to save incorrect data (name) to the database
    Given a vineyard "test vineyard" in "test region"
    And wine with details "test e wine", "is a nice wine", 2024, 95, 30, "test variety", "red", vineyard in database
    When the user submits "" in the name field
    Then data is not saved, error message name can't be blank
