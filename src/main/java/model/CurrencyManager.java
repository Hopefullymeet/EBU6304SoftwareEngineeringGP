package model;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * CurrencyManager - Manages currency conversion and formatting.
 * Provides utility methods for currency operations across the application.
 */
public class CurrencyManager {
    // Singleton instance
    private static CurrencyManager instance;
    
    // Currency symbols
    public static final String CNY = "¥ CNY (Chinese Yuan)";
    public static final String USD = "$ USD (US Dollar)";
    public static final String EUR = "€ EUR (Euro)";
    
    // Exchange rates relative to CNY (base currency)
    private final Map<String, Double> exchangeRates;
    
    // Currency symbols for formatting
    private final Map<String, String> currencySymbols;
    
    /**
     * Private constructor for singleton pattern
     */
    private CurrencyManager() {
        // Initialize exchange rates (CNY as base)
        exchangeRates = new HashMap<>();
        exchangeRates.put(CNY, 1.0);
        exchangeRates.put(USD, 0.1381); // 1 CNY = 0.1381 USD
        exchangeRates.put(EUR, 0.1269); // 1 CNY = 0.1269 EUR
        
        // Initialize currency symbols
        currencySymbols = new HashMap<>();
        currencySymbols.put(CNY, "¥");
        currencySymbols.put(USD, "$");
        currencySymbols.put(EUR, "€");
    }
    
    /**
     * Gets the singleton instance of CurrencyManager
     * @return The CurrencyManager instance
     */
    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }
    
    /**
     * Converts an amount from one currency to another
     * @param amount The amount to convert
     * @param fromCurrency The source currency
     * @param toCurrency The target currency
     * @return The converted amount
     */
    public double convert(double amount, String fromCurrency, String toCurrency) {
        // If currencies are the same, no conversion needed
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        // Convert to base currency (CNY) first if needed
        double amountInBase = amount;
        if (!fromCurrency.equals(CNY)) {
            amountInBase = amount / exchangeRates.get(fromCurrency);
        }
        
        // Convert from base to target currency
        return amountInBase * exchangeRates.get(toCurrency);
    }
    
    /**
     * Formats a currency amount according to the specified currency
     * @param amount The amount to format
     * @param currency The currency to use for formatting
     * @return The formatted currency string
     */
    public String format(double amount, String currency) {
        String symbol = currencySymbols.getOrDefault(currency, "¥");
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return symbol + formatter.format(amount);
    }
    
    /**
     * Extracts the currency symbol from the full currency string
     * @param currency The full currency string (e.g., "¥ CNY (Chinese Yuan)")
     * @return The currency symbol (e.g., "¥")
     */
    public String getCurrencySymbol(String currency) {
        return currencySymbols.getOrDefault(currency, "¥");
    }
    
    /**
     * Updates exchange rates to latest values (would normally fetch from an API)
     * This is a placeholder method - in a real application, this would connect to
     * a currency exchange rate API to get the latest rates
     */
    public void updateExchangeRates() {
        // In a real application, this would fetch rates from an external API
        // For this example, we'll just use hardcoded values
        
        // Example updated rates
        exchangeRates.put(USD, 0.1385); // Updated rate: 1 CNY = 0.1385 USD
        exchangeRates.put(EUR, 0.1272); // Updated rate: 1 CNY = 0.1272 EUR
    }
    
    /**
     * Gets the current exchange rate between two currencies
     * @param fromCurrency The source currency
     * @param toCurrency The target currency
     * @return The exchange rate
     */
    public double getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }
        
        // Calculate exchange rate
        double fromRate = exchangeRates.get(fromCurrency);
        double toRate = exchangeRates.get(toCurrency);
        
        return toRate / fromRate;
    }
} 