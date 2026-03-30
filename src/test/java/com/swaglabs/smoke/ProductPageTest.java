package com.swaglabs.smoke;

import com.swaglabs.base.BaseTest;
import com.swaglabs.pages.LoginPage;
import com.swaglabs.pages.ProductDetailPage;
import com.swaglabs.pages.ProductsPage;
import com.swaglabs.utils.TestData;
import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke Suite — Product page scenarios.
 * Covers:
 *   - Products page loads correctly
 *   - Product count
 *   - Sort options (A-Z, Z-A, Price low-high, Price high-low)
 *   - Product detail navigation
 *   - Product detail info integrity
 */
@Tag("smoke")
@Epic("Swag Labs Products")
@Feature("Product Page")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductPageTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeEach
    void login() {
        productsPage = new LoginPage()
                .openPage()
                .loginAs(TestData.STANDARD_USER, TestData.VALID_PASSWORD);
    }

    // ── Page Load ─────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Page load")
    @DisplayName("Products page loads with correct title")
    void testProductsPageLoads() {
        productsPage.verifyPageLoaded();
        assertEquals(TestData.TITLE_PRODUCTS, productsPage.getPageTitle(),
                "Page title mismatch");
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product inventory")
    @DisplayName("All 6 products are displayed")
    void testProductCount() {
        productsPage.verifyProductCount(TestData.TOTAL_PRODUCTS);
    }

    // ── Sorting ───────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @Story("Sorting")
    @DisplayName("Sort A-Z shows 'Sauce Labs Backpack' first")
    void testSortAZ() {
        productsPage.selectSortOption(TestData.SORT_AZ);
        productsPage.verifyFirstProductName(TestData.PRODUCT_BACKPACK);
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Story("Sorting")
    @DisplayName("Sort Z-A shows 'Test.allTheThings() T-Shirt (Red)' first")
    void testSortZA() {
        productsPage.selectSortOption(TestData.SORT_ZA);
        productsPage.verifyFirstProductName(TestData.PRODUCT_RED_TSHIRT);
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @Story("Sorting")
    @DisplayName("Sort price low-to-high — cheapest product appears first")
    void testSortPriceLowToHigh() {
        productsPage.selectSortOption(TestData.SORT_PRICE_ASC);
        double firstPrice = productsPage.getFirstItemPrice();
        productsPage.selectSortOption(TestData.SORT_PRICE_DESC);
        double lastPrice = productsPage.getFirstItemPrice();
        assertTrue(firstPrice < lastPrice,
                "Low-high sort: first price " + firstPrice +
                        " should be less than high-low first price " + lastPrice);
    }

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @Story("Sorting")
    @DisplayName("Sort price high-to-low — most expensive product appears first")
    void testSortPriceHighToLow() {
        productsPage.selectSortOption(TestData.SORT_PRICE_DESC);
        double firstPrice = productsPage.getFirstItemPrice();
        // Fleece Jacket is $49.99 — highest priced item
        assertTrue(firstPrice >= 49.99,
                "Expected highest price >= $49.99 but got $" + firstPrice);
    }

    // ── Product Detail ────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product detail")
    @DisplayName("Clicking a product name opens its detail page")
    void testNavigateToProductDetail() {
        ProductDetailPage detail = productsPage
                .openProductDetail(TestData.PRODUCT_BACKPACK);

        detail.verifyPageLoaded()
                .verifyProductName(TestData.PRODUCT_BACKPACK);
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @Story("Product detail")
    @DisplayName("Product detail page shows name, description, price and image (soft assertions)")
    void testProductDetailIntegrity() {
        ProductDetailPage detail = productsPage
                .openProductDetail(TestData.PRODUCT_BACKPACK);

        // Using AssertJ SoftAssertions — all assertions run regardless of failures
        SoftAssertions soft = new SoftAssertions();
        soft.assertThat(detail.getName())
                .as("Product name")
                .isNotEmpty();
        soft.assertThat(detail.getDescription())
                .as("Product description")
                .isNotEmpty();
        soft.assertThat(detail.getPrice())
                .as("Product price")
                .isGreaterThan(0.0);
        soft.assertThat(detail.getPriceText())
                .as("Price format")
                .startsWith("$");
        soft.assertAll();
    }

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @Story("Product detail")
    @DisplayName("Back button on detail page returns to products list")
    void testBackButtonFromDetailPage() {
        productsPage.openProductDetail(TestData.PRODUCT_BACKPACK)
                .goBack()
                .verifyPageLoaded();
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @Story("Product detail")
    @DisplayName("Adding item on detail page updates cart badge to 1")
    void testAddToCartFromDetailPage() {
        productsPage.openProductDetail(TestData.PRODUCT_BACKPACK)
                .addToCart()
                .verifyRemoveButtonVisible()
                .goBack()
                .verifyCartBadge(1);
    }
}