Feature: Tagging feature
  Scenario: As a consumer, I want to create a new tag and add it to my wine
    Given the user is logged in
    And is viewing detailed information about "Stoneleigh 2008 Sauvignon Blanc (Marlborough)"
    When the user creates new tag "dessert", chooses colour "lavender" and submits
    Then the new "lavender" "dessert" tag is added to the list of tags and the tag is added to the selected wine

  Scenario: As a consumer, I want to edit one of my created tags
    Given the user is logged in
    And is on the dashboard page
    And has created a new tag "dessert"
    When the user edits the tag, changes the name to "sweets", colour "gold" and submits
    Then the tag is updated and is called "sweets" with colour "gold"


