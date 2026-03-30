package com.swaglabs.utils;

/**
 * Centralised test data for Swag Labs (SauceDemo).
 * All credentials and shared strings live here so tests never embed magic strings.
 */
public final class TestData {

    private TestData() { /* utility class */ }

    // ── Base URL ──────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://www.saucedemo.com";

    // ── Valid Users ───────────────────────────────────────────────────────────
    public static final String STANDARD_USER     = "standard_user";

    // ── Locked-out User ───────────────────────────────────────────────────────
    public static final String LOCKED_OUT_USER    = "locked_out_user";

    // ── Password (same for all accounts) ─────────────────────────────────────
    public static final String VALID_PASSWORD     = "secret_sauce";
    public static final String INVALID_PASSWORD   = "wrong_password";
    public static final String INVALID_USERNAME   = "no_such_user";

    // ── Expected Error Messages ───────────────────────────────────────────────
    public static final String ERROR_LOCKED_OUT   =
            "Epic sadface: Sorry, this user has been locked out.";
    public static final String ERROR_INVALID_CREDS =
            "Epic sadface: Username and password do not match any user in this service";
    public static final String ERROR_USERNAME_REQUIRED =
            "Epic sadface: Username is required";
    public static final String ERROR_PASSWORD_REQUIRED =
            "Epic sadface: Password is required";

    // ── Checkout Info ─────────────────────────────────────────────────────────
    public static final String FIRST_NAME  = "Divine";
    public static final String LAST_NAME   = "Bayingana";
    public static final String POSTAL_CODE = "KG 100";

    // ── Products ──────────────────────────────────────────────────────────────
    public static final int TOTAL_PRODUCTS = 6;
    public static final String PRODUCT_BACKPACK         = "Sauce Labs Backpack";
    public static final String PRODUCT_BIKE_LIGHT       = "Sauce Labs Bike Light";
    public static final String PRODUCT_BOLT_TSHIRT      = "Sauce Labs Bolt T-Shirt";
    public static final String PRODUCT_FLEECE_JACKET    = "Sauce Labs Fleece Jacket";
    public static final String PRODUCT_ONESIE           = "Sauce Labs Onesie";
    public static final String PRODUCT_RED_TSHIRT       = "Test.allTheThings() T-Shirt (Red)";

    // ── Sort Options ──────────────────────────────────────────────────────────
    public static final String SORT_AZ        = "az";
    public static final String SORT_ZA        = "za";
    public static final String SORT_PRICE_ASC = "lohi";
    public static final String SORT_PRICE_DESC = "hilo";

    // ── Page Titles ───────────────────────────────────────────────────────────
    public static final String TITLE_PRODUCTS          = "Products";
}