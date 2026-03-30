package com.swaglabs.regression;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.CartPage;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.pages.ProductsPage;
import com.swaglabs.utils.TestData;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

/**
 * Regression Suite — Cart functionality.
 *
 * Covers:
 *   - Add single item
 *   - Add multiple items — badge count updates
 *   - Remove item from products page
 *   - Remove item from cart page
 *   - Cart persists items after navigating away
 *   - Continue shopping returns to products
 *   - Cart item details are correct
 */
@Tag("regression")
@Epic("Swag Labs Shopping Cart")
@Feature("Cart Functionality")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeEach
    void loginAndGoToProducts() {
        productsPage = new LoginPage()
                .openPage()
                .loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);
    }

    // ── Add to Cart ───────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add item")
    @DisplayName("Adding one item updates cart badge to 1")
    void testAddSingleItemToCart() {
        productsPage.addToCartByName(TestData.PRODUCT_BACKPACK)
                .verifyCartBadge(1);
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Add item")
    @DisplayName("Adding two items updates cart badge to 2")
    void testAddTwoItemsToCart() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .verifyCartBadge(2);
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @Story("Add item")
    @DisplayName("Adding all 6 products updates badge to 6")
    void testAddAllProductsToCart() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .addToCartByName(TestData.PRODUCT_BOLT_TSHIRT)
                .addToCartByName(TestData.PRODUCT_FLEECE_JACKET)
                .addToCartByName(TestData.PRODUCT_ONESIE)
                .addToCartByName(TestData.PRODUCT_RED_TSHIRT)
                .verifyCartBadge(6);
    }

    // ── Remove from Products Page ─────────────────────────────────────────────

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove item")
    @DisplayName("Removing an item from products page decrements the badge")
    void testRemoveItemFromProductsPage() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .verifyCartBadge(2)
                .removeFirstItemFromCart()
                .verifyCartBadge(1);
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove item")
    @DisplayName("Removing the only item hides the cart badge")
    void testRemoveOnlyItemHidesBadge() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .verifyCartBadge(1)
                .removeFirstItemFromCart()
                .verifyCartIsEmpty();
    }

    // ── Cart Page Content ─────────────────────────────────────────────────────

    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart contents")
    @DisplayName("Cart page shows the correct item(s) added from inventory")
    void testCartShowsCorrectItems() {
        CartPage cartPage = productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .goToCart();

        cartPage.verifyPageLoaded()
                .verifyCartItemCount(2)
                .verifyContainsProduct(TestData.PRODUCT_BACKPACK)
                .verifyContainsProduct(TestData.PRODUCT_BIKE_LIGHT);
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart contents")
    @DisplayName("Cart item details are correct — soft assertions on name and price")
    void testCartItemDetails() {
        CartPage cartPage = productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .goToCart();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(cartPage.getFirstItemName())
                .as("Item name in cart")
                .isEqualTo(TestData.PRODUCT_BACKPACK);
        soft.assertThat(cartPage.getFirstItemPrice())
                .as("Item price in cart")
                .isGreaterThan(0.0);
        soft.assertThat(cartPage.getItemCount())
                .as("Cart item count")
                .isEqualTo(1);
        soft.assertAll();
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart contents")
    @DisplayName("All cart items have quantity 1 by default")
    void testCartItemQuantity() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BOLT_TSHIRT)
                .goToCart()
                .verifyAllItemsQuantityIsOne();
    }

    // ── Remove from Cart Page ─────────────────────────────────────────────────

    @Test
    @Order(9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove item")
    @DisplayName("Removing an item from the cart page removes it from the list")
    void testRemoveItemFromCartPage() {
        CartPage cartPage = productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .goToCart();

        cartPage.verifyCartItemCount(2)
                .removeItemByName(TestData.PRODUCT_BACKPACK)
                .verifyCartItemCount(1)
                .verifyNotContainsProduct(TestData.PRODUCT_BACKPACK)
                .verifyContainsProduct(TestData.PRODUCT_BIKE_LIGHT);
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove item")
    @DisplayName("Removing all items results in an empty cart")
    void testRemoveAllItemsFromCart() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .goToCart()
                .removeAllItems()
                .verifyCartIsEmpty();
    }

    // ── Continue Shopping ─────────────────────────────────────────────────────

    @Test
    @Order(11)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    @DisplayName("'Continue Shopping' button returns to products page")
    void testContinueShopping() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .goToCart()
                .continueShopping()
                .verifyPageLoaded();
    }

    @Test
    @Order(12)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    @DisplayName("Cart retains items after navigating to products and back")
    void testCartRetainsItemsAfterNavigation() {
        productsPage
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .goToCart()
                .continueShopping()
                .verifyCartBadge(1)  // badge still shows 1
                .goToCart()
                .verifyCartItemCount(1);
    }
}