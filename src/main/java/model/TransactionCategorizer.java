package model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * TransactionCategorizer - Uses DeepSeek AI to categorize financial transactions.
 * This class analyzes transaction descriptions and assigns appropriate categories.
 */
public class TransactionCategorizer {
    
    // Categories for transactions
    public static final String[] CATEGORIES = {
        "Housing", "Transportation", "Food", "Utilities", 
        "Insurance", "Healthcare", "Savings", "Personal", 
        "Entertainment", "Education", "Clothing", "Gifts", 
        "Travel", "Income", "Investment", "Other"
    };
    
    // DeepSeek API client for AI categorization
    private DeepSeekAPI deepSeekAPI;
    
    /**
     * Constructor for TransactionCategorizer
     * @param apiKey The DeepSeek API key
     */
    public TransactionCategorizer(String apiKey) {
        this.deepSeekAPI = new DeepSeekAPI(apiKey);
        
        // Set a specialized system prompt for transaction categorization
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a financial transaction categorizer. " +
                "Your task is to analyze transaction descriptions and assign them to the most appropriate category. " +
                "Respond with the category name only. " +
                "Available categories: Housing, Transportation, Food, Utilities, Insurance, Healthcare, " +
                "Savings, Personal, Entertainment, Education, Clothing, Gifts, Travel, Income, Investment, Other.");
        
        // Replace the default system message with our specialized one
        deepSeekAPI.clearConversation();
        deepSeekAPI.addSystemMessage(systemMessage.get("content"));
    }
    
    /**
     * Categorize a single transaction description
     * @param description The transaction description
     * @param amount The transaction amount (positive for income, negative for expense)
     * @param callback Callback function that receives the category
     */
    public void categorizeTransaction(String description, double amount, Consumer<String> callback) {
        // Create a prompt that includes the description and amount
        String prompt = String.format(
            "Categorize this transaction into one of the predefined categories:\n" +
            "Description: %s\n" +
            "Amount: ¥%.2f\n" +
            "Respond with the category name only: ", 
            description, amount
        );
        
        // Send to the AI and process response
        deepSeekAPI.sendMessage(prompt, response -> {
            // Clean up the response to get just the category
            String category = cleanCategoryResponse(response);
            callback.accept(category);
        });
    }
    
    /**
     * Categorize multiple transactions in batch
     * @param transactions List of maps with 'description' and 'amount' keys
     * @param callback Callback function that receives a list of categories
     */
    public void categorizeTransactions(List<Map<String, Object>> transactions, 
                                      Consumer<List<String>> callback) {
        // Create a prompt with all transactions
        StringBuilder prompt = new StringBuilder();
        prompt.append("Categorize each of the following transactions into one of the predefined categories.\n");
        prompt.append("For each transaction, respond with the category name.\n\n");
        
        for (int i = 0; i < transactions.size(); i++) {
            Map<String, Object> transaction = transactions.get(i);
            String description = (String) transaction.get("description");
            double amount = ((Number) transaction.get("amount")).doubleValue();
            
            prompt.append(String.format("Transaction %d:\n", i + 1));
            prompt.append(String.format("Description: %s\n", description));
            prompt.append(String.format("Amount: ¥%.2f\n\n", amount));
        }
        
        prompt.append("Respond with a JSON object where the keys are transaction numbers and values are categories, like: ");
        prompt.append("{ \"1\": \"Food\", \"2\": \"Transportation\", etc. }");
        
        // Send to the AI and process response
        deepSeekAPI.sendMessage(prompt.toString(), response -> {
            try {
                // Extract JSON from response
                String jsonStr = extractJsonFromResponse(response);
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(jsonStr);
                
                // Convert to list of categories
                List<String> categories = new ArrayList<>();
                for (int i = 1; i <= transactions.size(); i++) {
                    String category = (String) jsonResponse.get(String.valueOf(i));
                    // If category is not found, use "Other"
                    categories.add(category != null ? category : "Other");
                }
                
                callback.accept(categories);
            } catch (Exception e) {
                e.printStackTrace();
                // If parsing fails, return a list of "Other" categories
                List<String> fallbackCategories = new ArrayList<>();
                for (int i = 0; i < transactions.size(); i++) {
                    fallbackCategories.add("Other");
                }
                callback.accept(fallbackCategories);
            }
        });
    }
    
    /**
     * Cleans the AI response to extract just the category
     */
    private String cleanCategoryResponse(String response) {
        // First try to match the response with one of our categories
        for (String category : CATEGORIES) {
            if (response.contains(category)) {
                return category;
            }
        }
        
        // If no category is found, return "Other"
        return "Other";
    }
    
    /**
     * Extracts JSON from the AI response
     */
    private String extractJsonFromResponse(String response) {
        // Look for content between { and }
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        
        if (start >= 0 && end >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        // If no JSON is found, return an empty JSON object
        return "{}";
    }
} 