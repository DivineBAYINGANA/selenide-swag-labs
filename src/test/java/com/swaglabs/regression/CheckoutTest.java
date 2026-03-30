package com.swaglabs.regression;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.*;
import com.swaglabs.utils.TestData;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression Suite — Checkout flow.
  * Covers:
 *   - Full happy-path checkout
 *   - Checkout field validations (missing first name, last name, zip)
 *   - Order summary — subtotal, tax, total maths
 *   - Cancel from checkout info returns to cart
 *   - Cancel from overview returns to products
 *   - Post-checkout: back-to-home returns to products
 */
@Tag("regression")
@Epic("Swag Labs Checkout")
@Feature("Checkout Flow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckoutTest extends BaseTest {

    /** Common setup: login → add Backpack → go to Cart → click Checkout */
    private CheckoutStepOnePage beginCheckout() {
        return new LoginPage()
                .openPage()
                .loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD)
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .goToCart()
                .proceedToCheckout();
    }

    /** Login and add two products to cart. */
    private CheckoutStepOnePage beginCheckoutWithTwoItems() {
        return new LoginPage()
                .openPage()
                .loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD)
                .addToCartByName(TestData.PRODUCT_BACKPACK)
                .addToCartByName(TestData.PRODUCT_BIKE_LIGHT)
                .goToCart()
                .proceedToCheckout();
    }

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Happy path checkout")
    @DisplayName("Full checkout flow completes successfully and shows thank-you page")
    void testCompleteCheckoutFlow() {
        beginCheckout()
                .verifyPageLoaded()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue()
                .verifyPageLoaded()
                .finishOrder()
                .verifyPageLoaded()
                .verifyThankYouMessage()
                .verifyDispatchText();
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Happy path checkout")
    @DisplayName("After checkout completion, 'Back Home' returns to Products page")
    void testBackHomeAfterCheckout() {
        beginCheckout()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue()
                .finishOrder()
                .backToHome()
                .verifyPageLoaded();
    }

    // ── Checkout Step 1 Validations ───────────────────────────────────────────

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Field validation")
    @DisplayName("Missing first name shows 'First Name is required' error")
    void testCheckoutMissingFirstName() {
        CheckoutStepOnePage step1 = beginCheckout()
                .enterLastName(TestData.LAST_NAME)
                .enterPostalCode(TestData.POSTAL_CODE)
                .clickContinueExpectingError();

        assertTrue(step1.isErrorDisplayed(), "Error should be visible");
        step1.verifyErrorMessage("First Name is required");
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Field validation")
    @DisplayName("Missing last name shows 'Last Name is required' error")
    void testCheckoutMissingLastName() {
        CheckoutStepOnePage step1 = beginCheckout()
                .enterFirstName(TestData.FIRST_NAME)
                .enterPostalCode(TestData.POSTAL_CODE)
                .clickContinueExpectingError();

        assertTrue(step1.isErrorDisplayed(), "Error should be visible");
        step1.verifyErrorMessage("Last Name is required");
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Field validation")
    @DisplayName("Missing postal code shows 'Postal Code is required' error")
    void testCheckoutMissingPostalCode() {
        CheckoutStepOnePage step1 = beginCheckout()
                .enterFirstName(TestData.FIRST_NAME)
                .enterLastName(TestData.LAST_NAME)
                .clickContinueExpectingError();

        assertTrue(step1.isErrorDisplayed(), "Error should be visible");
        step1.verifyErrorMessage("Postal Code is required");
    }

    // ── Checkout Overview (Summary) ───────────────────────────────────────────

    @Test
    @Order(6)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Order summary")
    @DisplayName("Checkout overview shows correct item(s) count")
    void testCheckoutOverviewItemCount() {
        beginCheckoutWithTwoItems()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue()
                .verifyPageLoaded()
                .verifyItemCount(2);
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Order summary")
    @DisplayName("Checkout overview: subtotal + tax = total (mathematical integrity)")
    void testCheckoutTotalsCalculation() {
        CheckoutOverviewPage overview = beginCheckoutWithTwoItems()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue();

        overview.verifyPageLoaded()
                .verifyTotalIsPositive()
                .verifyTotalsMatch();  // hard assertion inside the page method

        // Additional soft assertions for the individual line items
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(overview.getSubtotal())
                .as("Subtotal")
                .isGreaterThan(0.0);
        soft.assertThat(overview.getTax())
                .as("Tax")
                .isGreaterThan(0.0);
        soft.assertThat(overview.getTotal())
                .as("Total")
                .isEqualByComparingTo(
                        Math.round((overview.getSubtotal() + overview.getTax()) * 100.0) / 100.0);
        soft.assertAll();
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @Story("Order summary")
    @DisplayName("Checkout overview summary labels display dollar amounts")
    void testCheckoutSummaryFormat() {
        CheckoutOverviewPage overview = beginCheckout()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue();

        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(overview.getSubtotalText()).as("Subtotal label").startsWith("Item total: $");
        soft.assertThat(overview.getTaxText()).as("Tax label").startsWith("Tax: $");
        soft.assertThat(overview.getTotalText()).as("Total label").startsWith("Total: $");
        soft.assertAll();
    }

    // ── Cancel Navigation ─────────────────────────────────────────────────────

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    @DisplayName("Cancel on checkout step 1 returns to cart with items intact")
    void testCancelCheckoutStep1ReturnsToCart() {
        beginCheckout()
                .enterFirstName(TestData.FIRST_NAME)
                .cancelCheckout()
                .verifyPageLoaded()
                .verifyCartItemCount(1)
                .verifyContainsProduct(TestData.PRODUCT_BACKPACK);
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    @DisplayName("Cancel on checkout overview returns to products page")
    void testCancelCheckoutOverviewReturnsToProducts() {
        beginCheckout()
                .fillInfo(TestData.FIRST_NAME, TestData.LAST_NAME, TestData.POSTAL_CODE)
                .clickContinue()
                .cancelOrder()
                .verifyPageLoaded();
    }
}