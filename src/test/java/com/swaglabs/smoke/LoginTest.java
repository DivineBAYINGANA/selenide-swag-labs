package com.swaglabs.smoke;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.pages.ProductsPage;
import com.swaglabs.utils.TestData;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke Suite — Login scenarios.
 * Covers:
 *   - Happy-path login with valid credentials
 *   - Locked-out user
 *   - Invalid username / password
 *   - Empty fields validation
 *   - Logout flow
 */
@Tag("smoke")
@Epic("Swag Labs Authentication")
@Feature("Login")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = new LoginPage().openPage();
    }

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Valid credentials")
    @DisplayName("Successful login with standard_user")
    void testSuccessfulLogin() {
        ProductsPage productsPage = loginPage.loginAs(
                TestData.STANDARD_USER, TestData.VALID_PASSWORD);

        productsPage.verifyPageLoaded();
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Logout")
    @DisplayName("User can logout and return to login page")
    void testLogout() {
        loginPage.loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD)
                .logout()
                .verifyPageLoaded();
    }

    // ── Negative Scenarios ────────────────────────────────────────────────────

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Locked-out user")
    @DisplayName("Locked-out user sees appropriate error message")
    void testLockedOutUser() {
        loginPage.attemptLoginAs(TestData.LOCKED_OUT_USER, TestData.VALID_PASSWORD)
                .verifyErrorMessage(TestData.ERROR_LOCKED_OUT);
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Invalid credentials")
    @DisplayName("Invalid username shows credential mismatch error")
    void testLoginWithInvalidUsername() {
        loginPage.attemptLoginAs(TestData.INVALID_USERNAME, TestData.VALID_PASSWORD)
                .verifyErrorMessage(TestData.ERROR_INVALID_CREDS);
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Invalid credentials")
    @DisplayName("Invalid password shows credential mismatch error")
    void testLoginWithInvalidPassword() {
        loginPage.attemptLoginAs(TestData.STANDARD_USER, TestData.INVALID_PASSWORD)
                .verifyErrorMessage(TestData.ERROR_INVALID_CREDS);
    }

    // ── Empty Field Validation ────────────────────────────────────────────────

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @Story("Field validation")
    @DisplayName("Empty username field shows 'Username is required' error")
    void testLoginWithEmptyUsername() {
        loginPage.attemptLoginAs("", TestData.VALID_PASSWORD)
                .verifyErrorMessage(TestData.ERROR_USERNAME_REQUIRED);
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @Story("Field validation")
    @DisplayName("Empty password field shows 'Password is required' error")
    void testLoginWithEmptyPassword() {
        loginPage.attemptLoginAs(TestData.STANDARD_USER, "")
                .verifyErrorMessage(TestData.ERROR_PASSWORD_REQUIRED);
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.MINOR)
    @Story("Field validation")
    @DisplayName("Both fields empty shows 'Username is required' error")
    void testLoginWithBothFieldsEmpty() {
        loginPage.attemptLoginAs("", "")
                .verifyErrorMessage(TestData.ERROR_USERNAME_REQUIRED);
    }

    // ── Soft Assertion Example ────────────────────────────────────────────────

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @Story("UI validation")
    @DisplayName("Login page elements are all visible on load (soft assertions)")
    void testLoginPageElementsVisible() {
        // Soft assertions: all checks run even if some fail
        assertAll("Login page elements",
                () -> loginPage.verifyPageLoaded(),
                () -> assertTrue(loginPage.getUsernameInput().isDisplayed(),
                        "Username input should be visible"),
                () -> assertTrue(loginPage.getPasswordInput().isDisplayed(),
                        "Password input should be visible")
        );
    }

    // ── Error Dismissal ───────────────────────────────────────────────────────

    @Test
    @Order(10)
    @Severity(SeverityLevel.MINOR)
    @Story("Error handling")
    @DisplayName("Error message can be dismissed with the X button")
    void testErrorMessageCanBeDismissed() {
        loginPage.attemptLoginAs(TestData.LOCKED_OUT_USER, TestData.VALID_PASSWORD);
        assertTrue(loginPage.isErrorDisplayed(), "Error should be visible before dismissal");

        loginPage.dismissError();
        loginPage.verifyNoError();
    }
}