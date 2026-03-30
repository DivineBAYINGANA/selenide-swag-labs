package com.swaglabs.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for Checkout Step 1 — Customer Information.
 */
public class CheckoutStepOnePage {

    private final SelenideElement pageTitle    = $("[data-test='title']");
    private final SelenideElement firstNameField = $("[data-test='firstName']");
    private final SelenideElement lastNameField  = $("[data-test='lastName']");
    private final SelenideElement postalField    = $("[data-test='postalCode']");
    private final SelenideElement continueButton = $("[data-test='continue']");
    private final SelenideElement cancelButton   = $("[data-test='cancel']");
    private final SelenideElement errorContainer = $("[data-test='error']");

    @Step("Enter first name: {firstName}")
    public CheckoutStepOnePage enterFirstName(String firstName) {
        firstNameField.shouldBe(visible).setValue(firstName);
        return this;
    }

    @Step("Enter last name: {lastName}")
    public CheckoutStepOnePage enterLastName(String lastName) {
        lastNameField.shouldBe(visible).setValue(lastName);
        return this;
    }

    @Step("Enter postal code: {postalCode}")
    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        postalField.shouldBe(visible).setValue(postalCode);
        return this;
    }

    @Step("Fill checkout info — {firstName} {lastName}, {postalCode}")
    public CheckoutStepOnePage fillInfo(String firstName, String lastName, String postalCode) {
        return enterFirstName(firstName)
                .enterLastName(lastName)
                .enterPostalCode(postalCode);
    }

    @Step("Click Continue to proceed to checkout overview")
    public CheckoutOverviewPage clickContinue() {
        continueButton.shouldBe(enabled).click();
        return new CheckoutOverviewPage();
    }

    @Step("Attempt to continue (may show validation error)")
    public CheckoutStepOnePage clickContinueExpectingError() {
        continueButton.click();
        return this;
    }

    @Step("Cancel checkout and return to cart")
    public CartPage cancelCheckout() {
        cancelButton.shouldBe(visible).click();
        return new CartPage();
    }

    @Step("Verify checkout step 1 page is loaded")
    public CheckoutStepOnePage verifyPageLoaded() {
        pageTitle.shouldBe(visible).shouldHave(exactText("Checkout: Your Information"));
        firstNameField.shouldBe(visible);
        lastNameField.shouldBe(visible);
        postalField.shouldBe(visible);
        continueButton.shouldBe(visible);
        return this;
    }

    @Step("Verify error message: {expectedText}")
    public void verifyErrorMessage(String expectedText) {
        errorContainer.shouldBe(visible).shouldHave(text(expectedText));
    }

    public boolean isErrorDisplayed() { return errorContainer.isDisplayed(); }
}