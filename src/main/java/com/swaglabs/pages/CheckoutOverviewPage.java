package com.swaglabs.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object for Checkout Step 2 — Order Overview / Summary.
 */
public class CheckoutOverviewPage {

    private final SelenideElement pageTitle     = $("[data-test='title']");
    private final SelenideElement finishButton  = $("[data-test='finish']");
    private final SelenideElement cancelButton  = $("[data-test='cancel']");
    private final SelenideElement subtotalLabel = $(".summary_subtotal_label");
    private final SelenideElement taxLabel      = $(".summary_tax_label");
    private final SelenideElement totalLabel    = $(".summary_total_label");
    private final ElementsCollection cartItems  = $$(".cart_item");

    @Step("Click Finish to complete the order")
    public CheckoutCompletePage finishOrder() {
        finishButton.shouldBe(visible).shouldBe(enabled).click();
        return new CheckoutCompletePage();
    }

    @Step("Cancel and return to products page")
    public ProductsPage cancelOrder() {
        cancelButton.shouldBe(visible).click();
        return new ProductsPage();
    }

    @Step("Verify checkout overview page is loaded")
    public CheckoutOverviewPage verifyPageLoaded() {
        pageTitle.shouldBe(visible).shouldHave(exactText("Checkout: Overview"));
        finishButton.shouldBe(visible);
        subtotalLabel.shouldBe(visible);
        totalLabel.shouldBe(visible);
        return this;
    }

    @Step("Verify item count in summary is {expectedCount}")
    public void verifyItemCount(int expectedCount) {
        cartItems.shouldHave(com.codeborne.selenide.CollectionCondition.size(expectedCount));
    }

    @Step("Verify total is not zero")
    public CheckoutOverviewPage verifyTotalIsPositive() {
        double total = getTotal();
        assert total > 0 : "Expected total > 0 but was " + total;
        return this;
    }

    @Step("Verify subtotal + tax equals total")
    public void verifyTotalsMatch() {
        double subtotal = getSubtotal();
        double tax      = getTax();
        double total    = getTotal();
        double expected = Math.round((subtotal + tax) * 100.0) / 100.0;
        assert Math.abs(expected - total) < 0.01
                : "Expected total " + expected + " but got " + total;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public double getSubtotal() {
        String raw = subtotalLabel.getText().replace("Item total: $", "");
        return Double.parseDouble(raw);
    }

    public double getTax() {
        String raw = taxLabel.getText().replace("Tax: $", "");
        return Double.parseDouble(raw);
    }

    public double getTotal() {
        String raw = totalLabel.getText().replace("Total: $", "");
        return Double.parseDouble(raw);
    }

    public String getSubtotalText() { return subtotalLabel.getText(); }
    public String getTaxText()      { return taxLabel.getText();      }
    public String getTotalText()    { return totalLabel.getText();    }

}