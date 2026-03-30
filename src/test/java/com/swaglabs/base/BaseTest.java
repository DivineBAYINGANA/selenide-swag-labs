package com.swaglabs.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;

/**
 * Base test class.
 * Responsibilities:
 *   - Configure Selenide browser, timeout, headless mode, and base URL.
 *   - Register the AllureSelenide listener (screenshots + page source on failure).
 *   - Close the WebDriver after each test.
 * All test classes extend this class.
 */
public abstract class BaseTest {

    /**
     * Called once before the first test in the class.
     * Registers the AllureSelenide listener so every test step,
     * screenshot, and page source is attached to the Allure report.
     */
    @BeforeAll
    static void registerAllureListener() {
        // Add only once — SelenideLogger is global
        if (!SelenideLogger.hasListener("allure")) {
            SelenideLogger.addListener("allure",
                    new AllureSelenide()
                            .screenshots(true)        // screenshot on every step
                            .savePageSource(true));   // save HTML source on failure
        }
    }

    /**
     * Runs before each individual test method.
     * Re-applies Selenide Configuration so each test starts clean.
     */
    @BeforeEach
    void configureSelenide() {
        // ── Browser ──────────────────────────────────────────────────────────
        Configuration.browser = System.getProperty("browser", "chrome");

        // ── Headless (default: true so Docker/CI work without a display) ─────
        Configuration.headless = Boolean.parseBoolean(
                System.getProperty("headless", "true"));

        // ── Base URL ─────────────────────────────────────────────────────────
        Configuration.baseUrl = "https://www.saucedemo.com";

        // ── Timeouts ─────────────────────────────────────────────────────────
        Configuration.timeout         = 10_000;   // element wait timeout (ms)
        Configuration.pageLoadTimeout = 30_000;   // page load timeout (ms)

        // ── Screenshots & Page Source (Selenide native, separate from Allure) ─
        Configuration.screenshots    = true;
        Configuration.savePageSource = true;
        Configuration.reportsFolder  = "target/selenide-reports";

        // ── Remote WebDriver (e.g. Selenium Grid / Docker Compose) ────────────
        String remote = System.getProperty("selenide.remote", "");
        if (!remote.isEmpty()) {
            Configuration.remote = remote;
            // When using a remote grid, browser capabilities must be sent as options
            Configuration.browserCapabilities.setCapability(
                    "se:name", "Swag Labs Tests");
        }
    }

    /**
     * Closes the WebDriver after each test to guarantee isolation.
     * Selenide will still capture a screenshot if the test already failed.
     */
    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}