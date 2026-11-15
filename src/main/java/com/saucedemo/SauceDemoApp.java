package com.saucedemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SauceDemoApp {

    private SauceDemoApp() {}

    // =========================================================
    // 1. BASE PAGE
    // =========================================================

    public abstract static class BasePage {

        protected static final Duration TIMEOUT =
                Duration.ofSeconds(10);

        protected final WebDriver driver;
        protected final WebDriverWait wait;

        protected BasePage(WebDriver driver) {
            this.driver = driver;
            this.wait = new WebDriverWait(driver, TIMEOUT);
        }

        protected WebElement waitFor(By locator) {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(locator));
        }

        protected void click(By locator) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(locator)).click();
        }

        protected void click(WebElement element) {
            wait.until(
                    ExpectedConditions.elementToBeClickable(element)).click();
        }

        protected void typeInto(WebElement element, String text) {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();

            if (text != null) {
                element.sendKeys(text);
            }
        }
    }

    // =========================================================
    // 2. LOGIN PAGE
    // =========================================================

    public static class LoginPage extends BasePage {

        public static final String URL =
                "https://www.saucedemo.com/";

        @FindBy(id = "user-name")
        private WebElement usernameField;

        @FindBy(id = "password")
        private WebElement passwordField;

        @FindBy(id = "login-button")
        private WebElement loginButton;

        @FindBy(css = "[data-test='error']")
        private WebElement errorBanner;

        public LoginPage(WebDriver driver) {
            super(driver);
            PageFactory.initElements(driver, this);
        }

        public LoginPage open() {
            driver.get(URL);
            wait.until(
                    ExpectedConditions.visibilityOf(loginButton));
            return this;
        }

        public void login(String username, String password) {
            typeInto(usernameField, username);
            typeInto(passwordField, password);
            click(loginButton);
        }

        public String getErrorText() {
            return wait.until(
                    ExpectedConditions.visibilityOf(errorBanner))
                    .getText();
        }
    }

    // =========================================================
    // 3. INVENTORY PAGE
    // =========================================================

    public static class InventoryPage extends BasePage {

        private static final By INVENTORY_LIST =
                By.cssSelector(".inventory_list");

        private static final By PRODUCT_CARDS =
                By.cssSelector(".inventory_item");

        private static final By ADD_BUTTONS =
                By.cssSelector(".inventory_item button");

        private static final By CART_BADGE =
                By.cssSelector(".shopping_cart_badge");

        private static final By CART_LINK =
                By.cssSelector(".shopping_cart_link");

        public InventoryPage(WebDriver driver) {
            super(driver);
        }

        public InventoryPage waitUntilLoaded() {
            waitFor(INVENTORY_LIST);
            return this;
        }

        public int productCount() {
            return driver.findElements(PRODUCT_CARDS).size();
        }

        public void addItemByIndex(int index) {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            PRODUCT_CARDS));

            driver.findElements(ADD_BUTTONS).get(index).click();
        }

        public int getCartBadgeCount() {
            List<WebElement> badges =
                    driver.findElements(CART_BADGE);

            return badges.isEmpty()
                    ? 0
                    : Integer.parseInt(
                            badges.get(0).getText().trim());
        }

        public void waitForCartBadge(int expected) {
            wait.until(d -> getCartBadgeCount() == expected);
        }

        public CartPage openCart() {
            click(CART_LINK);
            return new CartPage(driver);
        }
    }

    // =========================================================
    // 4. CART PAGE
    // =========================================================

    public static class CartPage extends BasePage {

        private static final By CART_ITEMS =
                By.cssSelector(".cart_item");

        private static final By CHECKOUT_BUTTON =
                By.id("checkout");

        public CartPage(WebDriver driver) {
            super(driver);
        }

        public int itemCount() {
            wait.until(
                    ExpectedConditions.urlContains("cart.html"));

            return driver.findElements(CART_ITEMS).size();
        }

        public CheckoutPage proceedToCheckout() {
            click(CHECKOUT_BUTTON);
            return new CheckoutPage(driver);
        }
    }

    // =========================================================
    // 5. CHECKOUT PAGE
    // =========================================================

    public static class CheckoutPage extends BasePage {

        private static final By FIRST_NAME =
                By.id("first-name");

        private static final By LAST_NAME =
                By.id("last-name");

        private static final By POSTAL =
                By.id("postal-code");

        private static final By CONTINUE =
                By.id("continue");

        private static final By ERROR =
                By.cssSelector("[data-test='error']");

        private static final By ITEM_TOTAL =
                By.cssSelector(".summary_subtotal_label");

        private static final By TAX =
                By.cssSelector(".summary_tax_label");

        private static final By TOTAL =
                By.cssSelector(".summary_total_label");

        public CheckoutPage(WebDriver driver) {
            super(driver);
        }

        public CheckoutPage fillInformation(
                String first, String last, String postal) {

            typeInto(waitFor(FIRST_NAME), first);
            typeInto(waitFor(LAST_NAME), last);
            typeInto(waitFor(POSTAL), postal);

            return this;
        }

        public CheckoutPage continueToOverview() {
            click(CONTINUE);
            return this;
        }

        public boolean isOverviewLoaded() {
            try {
                wait.until(
                        ExpectedConditions.urlContains(
                                "checkout-step-two.html"));

                return true;

            } catch (TimeoutException e) {
                return false;
            }
        }

        public String getErrorText() {
            return waitFor(ERROR).getText();
        }

        public BigDecimal getItemTotal() {
            return money(ITEM_TOTAL);
        }

        public BigDecimal getTax() {
            return money(TAX);
        }

        public BigDecimal getTotal() {
            return money(TOTAL);
        }

        private BigDecimal money(By locator) {
            String text = waitFor(locator).getText();

            return new BigDecimal(
                    text.replaceAll("[^0-9.]", ""));
        }
    }

    // =========================================================
    // 6. FIELD VALIDATOR
    // BVA - Boundary Value Analysis
    // ECP - Equivalence Class Partitioning
    // CFG - Control Flow Graph
    // =========================================================

    public static final class FieldValidator {

        public static final int QUANTITY_MIN = 1;
        public static final int QUANTITY_MAX = 6;

        public static final int NAME_MIN_LENGTH = 1;
        public static final int NAME_MAX_LENGTH = 30;

        public static final int POSTAL_LENGTH = 5;

        public static final String NAME_REQUIRED =
                "Error: First Name is required";

        public static final String NAME_TOO_LONG =
                "Error: First Name is too long";

        public static final int[] CFG_QUANTITY = {7, 8};
        public static final int[] CFG_NAME = {9, 11};
        public static final int[] CFG_POSTAL = {10, 13};

        private FieldValidator() {}

        // QUANTITY VALIDATION

        public static boolean isValidQuantity(int quantity) {

            if (quantity < QUANTITY_MIN) {
                return false;
            }

            if (quantity > QUANTITY_MAX) {
                return false;
            }

            return true;
        }

        // FIRST NAME VALIDATION

        public static String validateName(String name) {

            if (name == null) {
                return NAME_REQUIRED;
            }

            String trimmed = name.trim();

            if (trimmed.isEmpty()) {
                return NAME_REQUIRED;
            }

            if (trimmed.length() > NAME_MAX_LENGTH) {
                return NAME_TOO_LONG;
            }

            return null;
        }

        // POSTAL CODE VALIDATION

        public static boolean isValidPostalCode(String code) {

            if (code == null) {
                return false;
            }

            if (code.length() != POSTAL_LENGTH) {
                return false;
            }

            for (int i = 0; i < code.length(); i++) {

                char c = code.charAt(i);

                if (c < '0' || c > '9') {
                    return false;
                }
            }

            return true;
        }
    }

    // =========================================================
    // 7. CHECKOUT CALCULATOR
    // WHITE-BOX UNIT TESTING
    // =========================================================

    public static final class CheckoutCalculator {

        public static final BigDecimal TAX_RATE =
                new BigDecimal("0.08");

        private CheckoutCalculator() {}

        public static BigDecimal calculateTax(
                BigDecimal subtotal) {

            if (subtotal == null) {
                throw new IllegalArgumentException(
                        "subtotal must not be null");
            }

            if (subtotal.signum() < 0) {
                throw new IllegalArgumentException(
                        "subtotal must be >= 0");
            }

            return subtotal.multiply(TAX_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        public static BigDecimal calculateTotal(
                BigDecimal subtotal) {

            return subtotal.add(calculateTax(subtotal));
        }
    }
}