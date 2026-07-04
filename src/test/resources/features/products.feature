Feature: Products Page Navigation

  Scenario: User can navigate to All Products page
    Given the user is on the home page
    Then the home page should be displayed
    When the user navigates to the Products page
    Then the All Products header should be displayed
