package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * BudgetInsights - Class for generating AI-powered insights for budgeting.
 * Uses DeepSeekAPI to generate personalized budget recommendations based on user spending patterns.
 */
public class BudgetInsights {
    private String apiKey;
    private DeepSeekAPI deepSeekAPI;
    
    /**
     * Constructor for BudgetInsights
     * @param apiKey The API key for DeepSeekAPI
     */
    public BudgetInsights(String apiKey) {
        this.apiKey = apiKey;
        
        // If the API key is empty, use the default constructor which will use the default API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            this.deepSeekAPI = new DeepSeekAPI(); // Uses default key
            System.out.println("BudgetInsights: Using default API key");
        } else {
            this.deepSeekAPI = new DeepSeekAPI(apiKey);
            System.out.println("BudgetInsights: Using provided API key: " + apiKey.substring(0, Math.min(5, apiKey.length())) + "...");
        }
    }
    
    /**
     * Generates general budget insights based on spending patterns
     * @param totalBudget The total budget amount
     * @param spentAmount The amount already spent
     * @param categoryBreakdown Map of categories to amounts
     * @param callback Callback to handle insights when generated
     */
    public void generateGeneralInsights(double totalBudget, double spentAmount, 
                                        Map<String, Double> categoryBreakdown, 
                                        Consumer<List<String>> callback) {
        try {
            // Create the prompt for the AI
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a financial advisor AI specializing in budget analysis and recommendations. ");
            prompt.append("Based on the following financial data, provide 3-5 concise, actionable insights and recommendations. ");
            prompt.append("Each insight should be a single paragraph and focused on helping the user manage their finances better. ");
            prompt.append("\n\nFinancial Data:");
            prompt.append("\nTotal Monthly Budget: ¥").append(String.format("%.2f", totalBudget));
            prompt.append("\nTotal Spent Amount: ¥").append(String.format("%.2f", spentAmount));
            prompt.append("\nRemaining Budget: ¥").append(String.format("%.2f", totalBudget - spentAmount));
            
            // Add category breakdown
            prompt.append("\n\nSpending by Category:");
            for (Map.Entry<String, Double> entry : categoryBreakdown.entrySet()) {
                prompt.append("\n- ").append(entry.getKey()).append(": ¥").append(String.format("%.2f", entry.getValue()));
            }
            
            // Additional context for better insights
            prompt.append("\n\nConsider the following in your insights:");
            prompt.append("\n1. Highlight categories with unusually high spending");
            prompt.append("\n2. Suggest specific savings opportunities");
            prompt.append("\n3. Note if the user is on track to stay within budget");
            prompt.append("\n4. Provide a practical tip related to the largest spending category");
            prompt.append("\n5. Consider Chinese cultural context and holidays if relevant");
            
            // Request format instructions
            prompt.append("\n\nFormat your response as a list of insights, with each insight being a short paragraph. ");
            prompt.append("Each insight should start with a bullet point. ");
            prompt.append("DO NOT include introductions, conclusions, or any text that isn't a direct insight or recommendation.");
            
            // Log the prompt
            System.out.println("Sending prompt to DeepSeekAPI: " + prompt.toString().substring(0, Math.min(200, prompt.toString().length())) + "...");
            
            // Create a list for partial results
            final List<String> partialResults = new ArrayList<>();
            
            // Make the API call with partial results handling
            deepSeekAPI.sendMessage(prompt.toString(), response -> {
                try {
                    // Check if we got a complete response or just a chunk
                    boolean isNewChunk = !response.contains("__STREAM_DONE__");
                    
                    if (isNewChunk) {
                        // This is just a new chunk, not a complete response
                        System.out.println("Received chunk: " + response.substring(0, Math.min(50, response.length())) + "...");
                        
                        // Send the raw chunk to the callback for immediate display
                        List<String> immediateChunk = new ArrayList<>();
                        immediateChunk.add(response);
                        if (callback != null) {
                            callback.accept(immediateChunk);
                        }
                    } else {
                        // We got the complete response, parse the final insights
                        System.out.println("Received complete response. Parsing insights...");
                        String cleanResponse = response.replace("__STREAM_DONE__", "");
                        List<String> insights = parseInsightsFromResponse(cleanResponse);
                        
                        // Log the number of insights found
                        System.out.println("Parsed " + insights.size() + " insights from response");
                        
                        // Call the callback with the complete insights
                        if (callback != null) {
                            callback.accept(insights);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing API response: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Create a list with the error message
                    List<String> errorInsights = new ArrayList<>();
                    errorInsights.add("• Error generating insights: " + e.getMessage() + ". Please try again later.");
                    
                    // Call the callback with the error message
                    if (callback != null) {
                        callback.accept(errorInsights);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error sending request to DeepSeekAPI: " + e.getMessage());
            e.printStackTrace();
            
            // Create a list with the error message
            List<String> errorInsights = new ArrayList<>();
            errorInsights.add("• Error connecting to AI service: " + e.getMessage() + ". Please try again later.");
            
            // Call the callback with the error message
            if (callback != null) {
                callback.accept(errorInsights);
            }
        }
    }
    
    /**
     * Generates seasonal budget planning recommendations
     * @param upcomingHoliday The name of the upcoming holiday or event
     * @param previousSpending Previous spending for this holiday (if available)
     * @param currentBudget Current available budget
     * @param callback Callback to handle recommendations when generated
     */
    public void generateSeasonalBudgetPlan(String upcomingHoliday, double previousSpending,
                                          double currentBudget, Consumer<List<String>> callback) {
        try {
            // Create the prompt for the AI
            StringBuilder prompt = new StringBuilder();
            prompt.append("You are a financial planning assistant specializing in holiday and seasonal budgeting. ");
            prompt.append("I need recommendations for an upcoming ").append(upcomingHoliday).append(" budget. ");
            
            prompt.append("\n\nFinancial Data:");
            prompt.append("\nPrevious spending for this occasion: ¥").append(String.format("%.2f", previousSpending));
            prompt.append("\nCurrent available budget: ¥").append(String.format("%.2f", currentBudget));
            
            prompt.append("\n\nPlease provide:");
            prompt.append("\n1. A recommended budget breakdown for ").append(upcomingHoliday);
            prompt.append("\n2. 3-4 specific cost-saving tips for this occasion");
            prompt.append("\n3. Areas where spending can be prioritized for maximum enjoyment");
            prompt.append("\n4. Any relevant cultural considerations for ").append(upcomingHoliday).append(" in China");
            
            // Format instructions
            prompt.append("\n\nFormat your response as a list of recommendations, with each being a short paragraph. ");
            prompt.append("Each recommendation should start with a bullet point. ");
            prompt.append("Be specific and practical with your advice.");
            
            // Log the prompt
            System.out.println("Sending seasonal prompt to DeepSeekAPI: " + prompt.toString().substring(0, Math.min(200, prompt.toString().length())) + "...");
            
            // Make the API call
            deepSeekAPI.sendMessage(prompt.toString(), response -> {
                try {
                    // Check if we got a complete response or just a chunk
                    boolean isNewChunk = !response.contains("__STREAM_DONE__");
                    
                    if (isNewChunk) {
                        // This is just a new chunk, not a complete response
                        System.out.println("Received chunk: " + response.substring(0, Math.min(50, response.length())) + "...");
                        
                        // Send the raw chunk to the callback for immediate display
                        List<String> immediateChunk = new ArrayList<>();
                        immediateChunk.add(response);
                        if (callback != null) {
                            callback.accept(immediateChunk);
                        }
                    } else {
                        // We got the complete response, parse the final recommendations
                        System.out.println("Received complete response. Parsing recommendations...");
                        String cleanResponse = response.replace("__STREAM_DONE__", "");
                        List<String> recommendations = parseInsightsFromResponse(cleanResponse);
                        
                        // Log the number of recommendations found
                        System.out.println("Parsed " + recommendations.size() + " recommendations from response");
                        
                        // Call the callback with the recommendations
                        if (callback != null) {
                            callback.accept(recommendations);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing API response: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Create a list with the error message
                    List<String> errorRecommendations = new ArrayList<>();
                    errorRecommendations.add("• Error generating holiday recommendations: " + e.getMessage() + ". Please try again later.");
                    
                    // Call the callback with the error message
                    if (callback != null) {
                        callback.accept(errorRecommendations);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error sending request to DeepSeekAPI: " + e.getMessage());
            e.printStackTrace();
            
            // Create a list with the error message
            List<String> errorRecommendations = new ArrayList<>();
            errorRecommendations.add("• Error connecting to AI service: " + e.getMessage() + ". Please try again later.");
            
            // Call the callback with the error message
            if (callback != null) {
                callback.accept(errorRecommendations);
            }
        }
    }
    
    /**
     * Parses insights from the AI response
     * @param response The AI response
     * @return List of insight strings
     */
    private List<String> parseInsightsFromResponse(String response) {
        List<String> insights = new ArrayList<>();
        
        // Log the raw response
        System.out.println("Parsing insights from response: " + response.substring(0, Math.min(200, response.length())) + "...");
        
        // Split the response by bullet points
        String[] parts = response.split("•");
        
        // Process each part, skipping the first one if it's empty
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.isEmpty() && i > 0) { // Skip first part if it's before any bullet points
                // Add bullet point back to the insight
                insights.add("• " + part);
            }
        }
        
        // If no bullet points were found, try splitting by new lines
        if (insights.isEmpty()) {
            System.out.println("No bullet points found, trying to split by new lines");
            String[] lines = response.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Check if line already starts with a bullet point or number
                    if (line.startsWith("•") || line.startsWith("-") || line.matches("^\\d+\\..*")) {
                        insights.add(line);
                    } else {
                        insights.add("• " + line);
                    }
                }
            }
        }
        
        // If still no insights, create a single insight with the whole response
        if (insights.isEmpty()) {
            System.out.println("No structured insights found, using entire response as one insight");
            insights.add("• " + response.trim());
        }
        
        return insights;
    }
} 