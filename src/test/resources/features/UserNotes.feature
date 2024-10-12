Feature: Editing Notes on Wines

  Scenario: User saves a note for a wine when logged in
    Given the user is logged in
    And the user is on the detailed view of "Yearlands 2010 Pinot Noir (Marlborough)" wine
    When the user enters "tasty" into the notes section
    Then the note "tasty" is saved to the user review of the "Yearlands 2010 Pinot Noir (Marlborough)" entry

  Scenario: User adds notes to a wine review
    Given a user is reviewing a wine with id 3
    When the user adds notes "Great wine, fruity" to the review
    Then the review should successfully save the notes
    And the notes in the review should be "Great wine, fruity"