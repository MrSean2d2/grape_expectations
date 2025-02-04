Feature: Filter feature
  Scenario: As a consumer, I want to filter out the wine into a category
    Given the user is on the base search page,
    When the user applies a year 2012 filter
    Then the system displays results of wine entries from 2012.

  Scenario: User searches for "Plume Pinot Noir"
    Given the user is on the base search page,
    When the user searches for "Plume Pinot Noir" in the search bar
    Then the system displays a filtered dataset which only contains "Plume Pinot Noir" wine entries

  Scenario: User searches for a wine that doesnt exist in the data set
    Given the user is on the base search page,
    When the user inputs non existent search "Non-Existent Wine"
    Then the system displays no entries in table

  Scenario: I want to be able to search by variety
    Given the user is on the base search page,
    When the user applies a variety filter "Chardonnay"
    Then the system displays results of wine entries of the variety "Chardonnay"

  Scenario: Filter wines by price range
    Given the user is on the base search page,
    When the user applies a minimum price range 10 and maximum price 40
    Then the system displays results of wine entries of price between 10 and 40

  Scenario: Apply year and minimum rating filters
    Given the user is on the base search page,
    When the user applies a 2013 year filter minimum rating filter of 94.
    Then the system displays only wines that from 2013 rate 94.

  Scenario: Apply multiple filters and a search
    Given the user is on the base search page,
    When the user applies filters variety "Chardonnay", colour "White", region name "Central Otago", year  2006, min price 22, max price 24, min rating 83, search bar "apple".
    Then the system displays results of wines with Variety "Chardonnay", colour "White", Region "Central Otago", Year 2006, Price between 22 and 24, Rating of 83 or higher, and containing "apple" in the name

  Scenario: No entries are shown when no entries match filter
    Given the user is on the base search page,
    When the user applies filters variety "Syrah" and region "Wairarapa"
    Then no entries are shown

  Scenario: Filter wines by colour
    Given the user is on the base search page,
    When the user applies a colour filter "White"
    Then the system displays results of only wine entries with colour "White"

  Scenario: Filter wines by tag
    Given the user is on the base search page,
    And the user has tagged 10 wines as "Favourite",
    When the user applies the tag filter "Favourite"
    Then the system displays the 10 reviewed wines of tag "Favourite"
