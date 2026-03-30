package com.swaglabs.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for the Swag Labs Product Detail page.
 */
public class ProductDetailPage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final SelenideElement productName   = $(".inventory_details_name");
    private final SelenideElement productDesc   = $(".inventory_details_desc");
    private final SelenideElement productPrice  = $(".inventory_details_price");
    private final SelenideElement productImage  = $(".inventory_details_img");
    private final SelenideElement addToCartBtn  = $("[data-test^='add-to-cart']");
    private final SelenideElement removeBtn     = $("[data-test^='remove']");
    private final SelenideElement backButton    = $("#back-to-products");

    // ── Actions ───────────────────────────────────────────────────────────────

    @Step("Add product to cart from detail page")
    public ProductDetailPage addToCart() {
        addToCartBtn.shouldBe(visible).shouldBe(enabled).click();
        return this;
    }

    @Step("Go back to products list")
    public ProductsPage goBack() {
        backButton.shouldBe(visible).click();
        return new ProductsPage();
    }

    // ── Assertions ────────────────────────────────────────────────────────────

    @Step("Verify product detail page is loaded")
    public ProductDetailPage verifyPageLoaded() {
        productName.shouldBe(visible);
        productDesc.shouldBe(visible);
        productPrice.shouldBe(visible);
        productImage.shouldBe(visible);
        addToCartBtn.shouldBe(visible);
        return this;
    }

    @Step("Verify product name is '{expectedName}'")
    public void verifyProductName(String expectedName) {
        productName.shouldHave(exactText(expectedName));
    }

    @Step("Verify 'Remove' button is visible after adding to cart")
    public ProductDetailPage verifyRemoveButtonVisible() {
        removeBtn.shouldBe(visible);
        return this;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public String getName()        { return productName.getText();  }
    public String getDescription() { return productDesc.getText();  }
    public String getPriceText()   { return productPrice.getText(); }

    public double getPrice() {
        return Double.parseDouble(productPrice.getText().replace("$", ""));
    }
}