package com.swaglabs.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

/**
 * Page Object for Checkout Complete — order confirmation screen.
 */
public class CheckoutCompletePage {

    private final SelenideElement pageTitle      = $("[data-test='title']");
    private final SelenideElement completeHeader = $(".complete-header");
    private final SelenideElement completeText   = $(".complete-text");
    private final SelenideElement ponyImage      = $("[data-test='pony-express']");
    private final SelenideElement backHomeButton = $("[data-test='back-to-products']");

    @Step("Click 'Back Home' to return to products")
    public ProductsPage backToHome() {
        backHomeButton.shouldBe(visible).click();
        return new ProductsPage();
    }

    @Step("Verify order confirmation page is loaded")
    public CheckoutCompletePage verifyPageLoaded() {
        pageTitle.shouldBe(visible).shouldHave(exactText("Checkout: Complete!"));
        completeHeader.shouldBe(visible);
        completeText.shouldBe(visible);
        ponyImage.shouldBe(visible);
        backHomeButton.shouldBe(visible);
        return this;
    }

    @Step("Verify confirmation header says 'Thank you for your order!'")
    public CheckoutCompletePage verifyThankYouMessage() {
        completeHeader.shouldHave(exactText("Thank you for your order!"));
        return this;
    }

    @Step("Verify dispatch confirmation text is shown")
    public void verifyDispatchText() {
        completeText.shouldHave(text("Your order has been dispatched"));
    }
}