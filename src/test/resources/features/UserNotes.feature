Feature: Editing Notes on Wines
  Scenario: User adds notes to a wine review
    Given a user is reviewing a wine with id 3
    When the user adds notes "Great wine, fruity" to the review
    Then the review should successfully save the notes
    And the notes in the review should be "Great wine, fruity"