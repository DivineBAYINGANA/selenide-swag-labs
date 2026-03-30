package com.swaglabs.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object for the Swag Labs Cart page.
 */
public class CartPage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final SelenideElement pageTitle        = $("[data-test='title']");
    private final SelenideElement checkoutButton   = $("[data-test='checkout']");
    private final SelenideElement continueShoppingBtn = $("[data-test='continue-shopping']");
    private final ElementsCollection cartItems     = $$(".cart_item");
    private final ElementsCollection itemNames     = $$(".inventory_item_name");
    private final ElementsCollection itemPrices    = $$(".inventory_item_price");
    private final ElementsCollection itemQtys      = $$(".cart_quantity");
    private final ElementsCollection removeButtons = $$("[data-test^='remove']");

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Proceed to checkout")
    public CheckoutStepOnePage proceedToCheckout() {
        checkoutButton.shouldBe(visible).shouldBe(enabled).click();
        return new CheckoutStepOnePage();
    }

    @Step("Continue shopping from cart")
    public ProductsPage continueShopping() {
        continueShoppingBtn.shouldBe(visible).click();
        return new ProductsPage();
    }

    @Step("Remove item '{itemName}' from cart")
    public CartPage removeItemByName(String itemName) {
        String dataTest = "remove-" + itemName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        $("[data-test='" + dataTest + "']").shouldBe(visible).click();
        return this;
    }

    @Step("Remove all items from cart")
    public CartPage removeAllItems() {
        // Collect all remove button data-test values first, then click each
        while (!removeButtons.isEmpty()) {
            removeButtons.first().click();
        }
        return this;
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Step("Verify cart page is loaded")
    public CartPage verifyPageLoaded() {
        pageTitle.shouldBe(visible).shouldHave(exactText("Your Cart"));
        checkoutButton.shouldBe(visible);
        continueShoppingBtn.shouldBe(visible);
        return this;
    }

    @Step("Verify cart has {expectedCount} item(s)")
    public CartPage verifyCartItemCount(int expectedCount) {
        cartItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(expectedCount));
        return this;
    }

    @Step("Verify cart is empty")
    public void verifyCartIsEmpty() {
        cartItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(0));
    }

    @Step("Verify cart contains product '{productName}'")
    public CartPage verifyContainsProduct(String productName) {
        itemNames.findBy(exactText(productName)).shouldBe(visible);
        return this;
    }

    @Step("Verify cart does not contain '{productName}'")
    public CartPage verifyNotContainsProduct(String productName) {
        itemNames.findBy(exactText(productName)).shouldNotBe(visible);
        return this;
    }

    @Step("Verify all items have quantity 1")
    public void verifyAllItemsQuantityIsOne() {
        for (SelenideElement qty : itemQtys) {
            qty.shouldHave(exactText("1"));
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int getItemCount()                 { return cartItems.size(); }

    public String getFirstItemName() {
        return itemNames.first().getText();
    }

    public double getFirstItemPrice() {
        return Double.parseDouble(itemPrices.first().getText().replace("$", ""));
    }
}