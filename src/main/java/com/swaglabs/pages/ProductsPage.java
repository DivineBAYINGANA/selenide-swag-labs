package com.swaglabs.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object for the Swag Labs Products (inventory) page.
 */
public class ProductsPage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final SelenideElement pageTitle        = $("[data-test='title']");
    private final SelenideElement cartLink         = $(".shopping_cart_link");
    private final SelenideElement cartBadge        = $(".shopping_cart_badge");
    private final SelenideElement sortDropdown     = $("[data-test='product_sort_container']");
    private final SelenideElement burgerMenuBtn    = $("#react-burger-menu-btn");
    private final SelenideElement logoutLink       = $("#logout_sidebar_link");
    private final ElementsCollection inventoryItems = $$(".inventory_item");
    private final ElementsCollection itemNames      = $$(".inventory_item_name");
    private final ElementsCollection itemPrices     = $$(".inventory_item_price");
    private final ElementsCollection removeBtns     = $$("[data-test^='remove']");

    // ── Navigation ────────────────────────────────────────────────────────────

    @Step("Navigate to the cart")
    public CartPage goToCart() {
        cartLink.shouldBe(visible).click();
        return new CartPage();
    }

    @Step("Open burger menu and logout")
    public LoginPage logout() {
        burgerMenuBtn.shouldBe(visible).click();
        logoutLink.shouldBe(visible).click();
        return new LoginPage();
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Add product '{productName}' to cart")
    public ProductsPage addToCartByName(String productName) {
        // Build the data-test attribute from the product name
        String dataTest = "add-to-cart-" + productName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        $("[data-test='" + dataTest + "']").shouldBe(visible).click();
        return this;
    }

    @Step("Remove first item from cart via products page")
    public ProductsPage removeFirstItemFromCart() {
        removeBtns.shouldHave(sizeGreaterThan(0));
        removeBtns.first().click();
        return this;
    }

    @Step("Select sort option: {option}")
    public void selectSortOption(String option) {
        sortDropdown.shouldBe(visible).selectOptionByValue(option);
    }

    @Step("Open product detail for '{productName}'")
    public ProductDetailPage openProductDetail(String productName) {
        itemNames.findBy(exactText(productName)).shouldBe(visible).click();
        return new ProductDetailPage();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Step("Verify Products page is loaded")
    public ProductsPage verifyPageLoaded() {
        pageTitle.shouldBe(visible).shouldHave(exactText("Products"));
        inventoryItems.shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Verify product count is {expectedCount}")
    public void verifyProductCount(int expectedCount) {
        inventoryItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(expectedCount));
    }

    @Step("Verify cart badge shows {expectedCount}")
    public ProductsPage verifyCartBadge(int expectedCount) {
        cartBadge.shouldBe(visible).shouldHave(exactText(String.valueOf(expectedCount)));
        return this;
    }

    @Step("Verify cart badge is not visible (cart is empty)")
    public void verifyCartIsEmpty() {
        cartBadge.shouldNotBe(visible);
    }

    @Step("Verify first product name is '{expectedName}'")
    public void verifyFirstProductName(String expectedName) {
        itemNames.first().shouldHave(exactText(expectedName));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String             getPageTitle()        { return pageTitle.getText(); }

    public double getFirstItemPrice() {
        String raw = itemPrices.first().getText().replace("$", "");
        return Double.parseDouble(raw);
    }
}