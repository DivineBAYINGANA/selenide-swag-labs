package com.swaglabs.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Page Object for the Swag Labs Login page.
 */
public class LoginPage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final SelenideElement logo         = $(".login_logo");
    private final SelenideElement usernameInput = $("#user-name");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement loginButton   = $("#login-button");
    private final SelenideElement errorContainer = $("[data-test='error']");
    private final SelenideElement errorButton    = $(".error-button");

    // ── Navigation ────────────────────────────────────────────────────────────

    @Step("Open Swag Labs login page")
    public LoginPage openPage() {
        open("/");
        return this;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Enter username: {username}")
    public void enterUsername(String username) {
        usernameInput.shouldBe(visible).clear();
        usernameInput.setValue(username);
    }

    @Step("Enter password")
    public void enterPassword(String password) {
        passwordInput.shouldBe(visible).clear();
        passwordInput.setValue(password);
    }

    @Step("Click the Login button")
    public ProductsPage clickLoginButton() {
        loginButton.shouldBe(enabled).click();
        return new ProductsPage();
    }

    /**
     * Full happy-path login — returns the Products page.
     */
    @Step("Login as '{username}'")
    public ProductsPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }

    /**
     * Attempt login expected to fail — stays on Login page.
     */
    @Step("Attempt login with invalid credentials for '{username}'")
    public LoginPage attemptLoginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        loginButton.click();
        return this;
    }

    @Step("Dismiss the error message")
    public void dismissError() {
        errorButton.shouldBe(visible).click();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Step("Verify login page is loaded")
    public LoginPage verifyPageLoaded() {
        logo.shouldBe(visible);
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        loginButton.shouldBe(visible).shouldBe(enabled);
        return this;
    }

    @Step("Verify error message contains: {expectedText}")
    public void verifyErrorMessage(String expectedText) {
        errorContainer.shouldBe(visible).shouldHave(text(expectedText));
    }

    @Step("Verify error message is not displayed")
    public void verifyNoError() {
        errorContainer.shouldNotBe(visible);
    }

    // ── Getters (for soft assertion use in tests) ─────────────────────────────

    public SelenideElement getUsernameInput()   { return usernameInput;  }
    public SelenideElement getPasswordInput()   { return passwordInput;  }
    public boolean isErrorDisplayed()           { return errorContainer.isDisplayed(); }
}