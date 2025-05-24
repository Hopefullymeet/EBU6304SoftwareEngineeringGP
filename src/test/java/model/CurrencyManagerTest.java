package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CurrencyManager class functionality
 */
public class CurrencyManagerTest {
    
    private CurrencyManager currencyManager;
    
    @BeforeEach
    public void setUp() {
        // Get the singleton instance and reset it for testing
        currencyManager = CurrencyManager.getInstance();
        // Reset to initial exchange rates by creating a new instance
        CurrencyManager.resetInstance();
        currencyManager = CurrencyManager.getInstance();
    }
    
    @Test
    @DisplayName("Test getInstance returns non-null singleton instance")
    public void testGetInstance() {
        assertNotNull(currencyManager, "getInstance should return a non-null CurrencyManager instance");
        
        // Verify that two calls return the same instance (singleton pattern)
        CurrencyManager secondInstance = CurrencyManager.getInstance();
        assertSame(currencyManager, secondInstance, "Two calls to getInstance should return the same instance");
    }
    
    @Test
    @DisplayName("Test conversion between the same currency")
    public void testConvertSameCurrency() {
        double amount = 100.0;
        double result = currencyManager.convert(amount, CurrencyManager.CNY, CurrencyManager.CNY);
        assertEquals(amount, result, 0.001, "Converting between the same currency should return the original amount");
    }
    
    @Test
    @DisplayName("Test CNY to USD currency conversion")
    public void testConvertCnyToUsd() {
        double amountInCny = 100.0;
        double expectedAmountInUsd = 13.81; // Based on hardcoded exchange rate: 1 CNY = 0.1381 USD
        
        double result = currencyManager.convert(amountInCny, CurrencyManager.CNY, CurrencyManager.USD);
        assertEquals(expectedAmountInUsd, result, 0.01, "CNY to USD conversion should use the correct exchange rate");
    }
    
    @Test
    @DisplayName("Test USD to CNY currency conversion")
    public void testConvertUsdToCny() {
        double amountInUsd = 50.0;
        double expectedAmountInCny = 50.0 / 0.1381; // Based on hardcoded exchange rate: 1 CNY = 0.1381 USD
        
        double result = currencyManager.convert(amountInUsd, CurrencyManager.USD, CurrencyManager.CNY);
        assertEquals(expectedAmountInCny, result, 0.01, "USD to CNY conversion should use the correct exchange rate");
    }
    
    @Test
    @DisplayName("Test USD to EUR currency conversion")
    public void testConvertUsdToEur() {
        double amountInUsd = 100.0;
        
        // First convert to CNY, then to EUR
        double amountInCny = 100.0 / 0.1381; // USD to CNY
        double expectedAmountInEur = amountInCny * 0.1269; // CNY to EUR
        
        double result = currencyManager.convert(amountInUsd, CurrencyManager.USD, CurrencyManager.EUR);
        assertEquals(expectedAmountInEur, result, 0.01, "USD to EUR conversion should first convert to base currency CNY, then to target currency");
    }
    
    @Test
    @DisplayName("Test currency formatting")
    public void testFormat() {
        double amount = 1234.56;
        
        String formattedCNY = currencyManager.format(amount, CurrencyManager.CNY);
        String formattedUSD = currencyManager.format(amount, CurrencyManager.USD);
        String formattedEUR = currencyManager.format(amount, CurrencyManager.EUR);
        
        assertEquals("¥1,234.56", formattedCNY, "CNY amount formatting should be correct");
        assertEquals("$1,234.56", formattedUSD, "USD amount formatting should be correct");
        assertEquals("€1,234.56", formattedEUR, "EUR amount formatting should be correct");
    }
    
    @Test
    @DisplayName("Test getting currency symbols")
    public void testGetCurrencySymbol() {
        assertEquals("¥", currencyManager.getCurrencySymbol(CurrencyManager.CNY), "CNY currency symbol should be ¥");
        assertEquals("$", currencyManager.getCurrencySymbol(CurrencyManager.USD), "USD currency symbol should be $");
        assertEquals("€", currencyManager.getCurrencySymbol(CurrencyManager.EUR), "EUR currency symbol should be €");
        assertEquals("¥", currencyManager.getCurrencySymbol("Unknown currency"), "Unknown currency symbol should default to ¥");
    }
    
    @Test
    @DisplayName("Test updating exchange rates")
    public void testUpdateExchangeRates() {
        // Record exchange rates before update
        double oldUsdRate = currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.USD);
        double oldEurRate = currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.EUR);
        
        // Update exchange rates
        currencyManager.updateExchangeRates();
        
        // Get updated exchange rates
        double newUsdRate = currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.USD);
        double newEurRate = currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.EUR);
        
        // Verify rates have been updated (hardcoded test values based on updateExchangeRates method)
        assertEquals(0.1385, newUsdRate, 0.0001, "USD exchange rate should be updated");
        assertEquals(0.1272, newEurRate, 0.0001, "EUR exchange rate should be updated");
        
        // Verify rates have actually changed
        assertNotEquals(oldUsdRate, newUsdRate, "Updated USD rate should be different from previous rate");
        assertNotEquals(oldEurRate, newEurRate, "Updated EUR rate should be different from previous rate");
    }
    
    @Test
    @DisplayName("Test getting exchange rates")
    public void testGetExchangeRate() {
        // Exchange rate between same currencies should be 1.0
        assertEquals(1.0, currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.CNY), 
                    0.0001, "Exchange rate between same currencies should be 1.0");
        assertEquals(1.0, currencyManager.getExchangeRate(CurrencyManager.USD, CurrencyManager.USD), 
                    0.0001, "Exchange rate between same currencies should be 1.0");
        
        // CNY to USD exchange rate
        assertEquals(0.1381, currencyManager.getExchangeRate(CurrencyManager.CNY, CurrencyManager.USD), 
                    0.0001, "CNY to USD exchange rate should be correct");
        
        // USD to CNY exchange rate (should be 1/0.1381)
        double expectedUsdToCny = 1.0 / 0.1381;
        assertEquals(expectedUsdToCny, currencyManager.getExchangeRate(CurrencyManager.USD, CurrencyManager.CNY), 
                    0.0001, "USD to CNY exchange rate should be the reciprocal of CNY to USD rate");
    }
} 