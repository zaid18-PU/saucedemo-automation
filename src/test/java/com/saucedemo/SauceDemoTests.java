package com.saucedemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SauceDemoTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Disable password-manager prompts in the test browser
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("https://www.saucedemo.com/");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Test 1: Successful login
    @Test
    void testSuccessfulLogin() {
        login();

        assertTrue(driver.getCurrentUrl().contains("inventory"));

        assertEquals(6, driver.findElements(
                By.className("inventory_item")).size());
    }

    // Test 2: Invalid login
    @Test
    void testInvalidLogin() {
        driver.findElement(By.id("user-name"))
                .sendKeys("wrong_user");

        driver.findElement(By.id("password"))
                .sendKeys("wrong_password");

        driver.findElement(By.id("login-button")).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")));

        assertTrue(error.isDisplayed());
    }

    // Test 3: Add product to cart
    @Test
    void testAddProductToCart() {
        login();

        driver.findElement(
                By.id("add-to-cart-sauce-labs-backpack")).click();

        WebElement cartBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.className("shopping_cart_badge")));

        assertEquals("1", cartBadge.getText());
    }

    // Test 4: Open shopping cart
    @Test
    void testOpenCart() {
        login();

        driver.findElement(
                By.id("add-to-cart-sauce-labs-backpack")).click();

        driver.findElement(
                By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.urlContains("cart"));

        assertEquals(1, driver.findElements(
                By.className("cart_item")).size());
    }

    // Test 5: Checkout with valid information
    @Test
    void testValidCheckoutInformation() {
        login();

        driver.findElement(
                By.id("add-to-cart-sauce-labs-backpack")).click();

        driver.findElement(
                By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("cart_item")));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("checkout"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("first-name"))).sendKeys("Test");

        driver.findElement(By.id("last-name"))
                .sendKeys("User");

        driver.findElement(By.id("postal-code"))
                .sendKeys("56001");

        driver.findElement(By.id("continue")).click();

        // Wait for either the next page or a validation error
        wait.until(d -> {
            boolean nextPage = d.getCurrentUrl()
                    .contains("checkout-step-two");

            boolean hasError = d.findElements(
                    By.cssSelector("[data-test='error']"))
                    .stream()
                    .anyMatch(WebElement::isDisplayed);

            return nextPage || hasError;
        });

        if (!driver.getCurrentUrl().contains("checkout-step-two")) {
            String errorMessage = driver.findElement(
                    By.cssSelector("[data-test='error']")).getText();

            fail("Checkout validation error: " + errorMessage);
        }

        assertTrue(driver.getCurrentUrl()
                .contains("checkout-step-two"));
    }

    // Test 6: Checkout with missing information
    @Test
    void testCheckoutWithMissingInformation() {
        login();

        driver.findElement(
                By.id("add-to-cart-sauce-labs-backpack")).click();

        driver.findElement(
                By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("checkout"))).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("continue"))).click();

        WebElement error = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='error']")));

        assertTrue(error.isDisplayed());
    }

    // Test 7: Quantity validation
    @Test
    void testQuantityValidation() {
        assertTrue(
                SauceDemoApp.FieldValidator.isValidQuantity(1));

        assertTrue(
                SauceDemoApp.FieldValidator.isValidQuantity(6));

        assertFalse(
                SauceDemoApp.FieldValidator.isValidQuantity(0));

        assertFalse(
                SauceDemoApp.FieldValidator.isValidQuantity(7));
    }

    // Test 8: Name validation
    @Test
    void testNameValidation() {
        assertNull(
                SauceDemoApp.FieldValidator.validateName("Test"));

        assertEquals(
                SauceDemoApp.FieldValidator.NAME_REQUIRED,
                SauceDemoApp.FieldValidator.validateName(""));

        assertEquals(
                SauceDemoApp.FieldValidator.NAME_REQUIRED,
                SauceDemoApp.FieldValidator.validateName(null));
    }

    // Test 9: Postal code validation
    @Test
    void testPostalCodeValidation() {
        assertTrue(
                SauceDemoApp.FieldValidator.isValidPostalCode("56001"));

        assertFalse(
                SauceDemoApp.FieldValidator.isValidPostalCode("1234"));

        assertFalse(
                SauceDemoApp.FieldValidator.isValidPostalCode("12A45"));
    }

    // Test 10: Checkout tax calculation
    @Test
    void testCheckoutTaxCalculation() {
        BigDecimal subtotal = new BigDecimal("10.00");

        BigDecimal tax =
                SauceDemoApp.CheckoutCalculator.calculateTax(subtotal);

        assertEquals(new BigDecimal("0.80"), tax);
    }

    // Reusable login method
    private void login() {
        driver.findElement(By.id("user-name"))
                .sendKeys("standard_user");

        driver.findElement(By.id("password"))
                .sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        wait.until(ExpectedConditions.urlContains("inventory"));
    }
}