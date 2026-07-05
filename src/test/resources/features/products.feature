Feature: Products Page Navigation

  Scenario: User can navigate to All Products page
    Given the user is on the home page
    Then the home page should be displayed
    When the user navigates to the Products page
    Then the All Products header should be displayed

  Scenario: User can add first product to cart and view it in the cart
    Given the user is on the home page
    When the user navigates to the Products page
    And the user captures the name of the first product
    And the user adds the first product to the cart
    And the user navigates to the cart page
    Then the captured product should be present in the cart