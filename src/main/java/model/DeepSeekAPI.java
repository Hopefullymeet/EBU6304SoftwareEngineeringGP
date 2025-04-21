package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import java.util.function.Consumer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.concurrent.CompletableFuture;

/**
 * DeepSeekAPI - Client for interacting with the DeepSeek Chat API.
 * This class handles sending messages to the DeepSeek AI and processing responses.
 */
public class DeepSeekAPI {
    
    // Available DeepSeek models
    public static final String MODEL_CHAT = "deepseek-chat";
    public static final String MODEL_REASONER = "deepseek-reasoner";
    
    // API Configuration
    private static final String BASE_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEFAULT_API_KEY = "sk-1d8d9e38ef114e0a8482255e57d54d68";
    private String apiKey;
    private String model;
    
    // Messages history - maintained as a list of role/content pairs
    private List<Map<String, String>> messages;
    
    /**
     * Constructor for DeepSeekAPI
     * @param apiKey Your DeepSeek API key
     */
    public DeepSeekAPI(String apiKey) {
        this(apiKey, MODEL_CHAT); // Default to deepseek-chat
    }
    
    /**
     * Default constructor for DeepSeekAPI using the default API key
     */
    public DeepSeekAPI() {
        this(DEFAULT_API_KEY, MODEL_CHAT);
    }
    
    /**
     * Constructor for DeepSeekAPI with model selection
     * @param apiKey Your DeepSeek API key
     * @param model The DeepSeek model to use (MODEL_CHAT or MODEL_REASONER)
     */
    public DeepSeekAPI(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.messages = new ArrayList<>();
        
        // Add the system prompt initially
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful financial advisor assistant. Provide concise, practical advice on personal finance, investments, budgeting, and savings. Use clear language and focus on actionable tips that can help users improve their financial situation.");
        this.messages.add(systemMessage);
    }
    
    /**
     * Get the current model being used
     * @return The model name
     */
    public String getModel() {
        return model;
    }
    
    /**
     * Set the model to use
     * @param model The model to use
     */
    public void setModel(String model) {
        this.model = model;
    }
    
    /**
     * Send a message to the DeepSeek API and get a response
     * @param userMessage The message from the user
     * @param responseCallback Callback function to handle the AI response
     */
    public void sendMessage(String userMessage, Consumer<String> responseCallback) {
        // Add user message to history
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        // Log the user message
        System.out.println("Sending message to DeepSeek API with model: " + model);
        System.out.println("Message content (first 100 chars): " + userMessage.substring(0, Math.min(100, userMessage.length())) + "...");
        
        // Create a StringBuilder to collect the full response
        final StringBuilder fullResponse = new StringBuilder();
        
        // Create a SwingWorker to handle API call in background
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    makeStreamingApiCall(chunk -> {
                        // Process each chunk
                        try {
                            if (chunk != null && !chunk.isEmpty()) {
                                // Log the chunk size
                                System.out.println("Received chunk of size: " + chunk.length());
                                
                                // Parse the JSON chunk
                                JSONParser parser = new JSONParser();
                                JSONObject jsonChunk = (JSONObject) parser.parse(chunk);
                                
                                JSONArray choices = (JSONArray) jsonChunk.get("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JSONObject firstChoice = (JSONObject) choices.get(0);
                                    JSONObject delta = (JSONObject) firstChoice.get("delta");
                                    
                                    if (delta != null && delta.containsKey("content")) {
                                        String content = (String) delta.get("content");
                                        if (content != null && !content.isEmpty()) {
                                            // Log the content
                                            System.out.println("Content chunk: " + content.substring(0, Math.min(30, content.length())) + "...");
                                            
                                            // Append to the full response
                                            fullResponse.append(content);
                                            // Publish this chunk to be processed
                                            publish(content);
                                        }
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            System.err.println("Error parsing chunk: " + e.getMessage());
                            if (chunk != null) {
                                System.err.println("Problematic chunk: " + chunk);
                            }
                            // Still try to use the chunk directly if parsing fails
                            if (chunk != null && chunk.contains("data:") && !chunk.contains("[DONE]")) {
                                String content = chunk.replace("data:", "").trim();
                                if (!content.isEmpty()) {
                                    publish(content);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error during streaming API call: " + e.getMessage());
                    e.printStackTrace();
                    publish("Error communicating with AI service: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                // Send each chunk to the callback as it arrives
                for (String chunk : chunks) {
                    responseCallback.accept(chunk);
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Add assistant response to message history
                    Map<String, String> assistantMsg = new HashMap<>();
                    assistantMsg.put("role", "assistant");
                    assistantMsg.put("content", fullResponse.toString());
                    messages.add(assistantMsg);
                    
                    // Log completion
                    System.out.println("API call completed. Total response length: " + fullResponse.length());
                    
                    // Send the STREAM_DONE marker
                    responseCallback.accept("\n\n__STREAM_DONE__");
                } catch (Exception e) {
                    System.err.println("Error in SwingWorker done: " + e.getMessage());
                    e.printStackTrace();
                    responseCallback.accept("\nError: " + e.getMessage() + "\n\n__STREAM_DONE__");
                }
            }
        }.execute();
    }
    
    /**
     * Make a streaming HTTP request to the DeepSeek API
     * @param chunkCallback Consumer function to process each chunk
     * @throws IOException If there's an error with the HTTP request
     */
    private void makeStreamingApiCall(Consumer<String> chunkCallback) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000); // 10 seconds timeout
        connection.setReadTimeout(30000);    // 30 seconds timeout
        
        // Convert messages list to JSON array
        JSONArray messagesArray = new JSONArray();
        for (Map<String, String> message : messages) {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("role", message.get("role"));
            jsonMessage.put("content", message.get("content"));
            messagesArray.add(jsonMessage);
        }
        
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", messagesArray);
        requestBody.put("stream", true); // Enable streaming
        requestBody.put("temperature", 0.7); // Add temperature
        requestBody.put("max_tokens", 1000); // Add max tokens
        
        // Get the request body as a string for logging
        String requestBodyString = requestBody.toJSONString();
        System.out.println("Request body (first 200 chars): " + requestBodyString.substring(0, Math.min(200, requestBodyString.length())) + "...");
        
        // Write request body to connection
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBodyString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Check response code
        int responseCode = connection.getResponseCode();
        System.out.println("API response code: " + responseCode);
        
        if (responseCode >= 200 && responseCode < 300) {
            // Success response, read streaming response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseLine = responseLine.trim();
                    
                    // Skip empty lines
                    if (responseLine.isEmpty()) {
                        continue;
                    }
                    
                    // Check if we've reached the end of the stream
                    if (responseLine.equals("data: [DONE]")) {
                        System.out.println("Stream completed. [DONE] marker received.");
                        // Send a special flag to indicate the stream is done
                        JSONObject doneObject = new JSONObject();
                        JSONArray choices = new JSONArray();
                        JSONObject choice = new JSONObject();
                        JSONObject delta = new JSONObject();
                        delta.put("content", "\n\n__STREAM_DONE__");
                        choice.put("delta", delta);
                        choices.add(choice);
                        doneObject.put("choices", choices);
                        chunkCallback.accept(doneObject.toJSONString());
                        continue;
                    }
                    
                    // Check if the line starts with "data: " and process the JSON
                    if (responseLine.startsWith("data: ")) {
                        String jsonData = responseLine.substring(6); // Remove "data: " prefix
                        chunkCallback.accept(jsonData);
                    } else {
                        // Log unexpected lines
                        System.out.println("Unexpected response line: " + responseLine);
                    }
                }
            }
        } else {
            // Error response
            System.err.println("API error response code: " + responseCode);
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
            }
            System.err.println("API error response: " + errorResponse.toString());
            
            // Return error as a JSON-like response
            JSONObject errorObject = new JSONObject();
            JSONArray choices = new JSONArray();
            JSONObject choice = new JSONObject();
            JSONObject delta = new JSONObject();
            delta.put("content", "Error: " + responseCode + " - " + errorResponse.toString());
            choice.put("delta", delta);
            choices.add(choice);
            errorObject.put("choices", choices);
            chunkCallback.accept(errorObject.toJSONString());
            
            // Send done marker
            JSONObject doneObject = new JSONObject();
            JSONArray doneChoices = new JSONArray();
            JSONObject doneChoice = new JSONObject();
            JSONObject doneDelta = new JSONObject();
            doneDelta.put("content", "\n\n__STREAM_DONE__");
            doneChoice.put("delta", doneDelta);
            doneChoices.add(doneChoice);
            doneObject.put("choices", doneChoices);
            chunkCallback.accept(doneObject.toJSONString());
            
            // Throw exception for higher-level error handling
            throw new IOException("API error: " + responseCode + " - " + errorResponse.toString());
        }
    }
    
    /**
     * Make the HTTP request to the DeepSeek API (non-streaming version)
     * @return JSON response from the API
     * @throws IOException If there's an error with the HTTP request
     * @throws ParseException If there's an error parsing the JSON
     */
    private String makeApiCall() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        
        // Convert messages list to JSON array
        JSONArray messagesArray = new JSONArray();
        for (Map<String, String> message : messages) {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("role", message.get("role"));
            jsonMessage.put("content", message.get("content"));
            messagesArray.add(jsonMessage);
        }
        
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", messagesArray);
        requestBody.put("stream", false);
        
        // Write request body to connection
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        return response.toString();
    }
    
    /**
     * Clear the conversation history
     */
    public void clearConversation() {
        messages.clear();
        
        // Re-add the system message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful financial advisor assistant. Provide concise, practical advice on personal finance, investments, budgeting, and savings. Use clear language and focus on actionable tips that can help users improve their financial situation. If possible, tailor your advice to the Chinese financial context when appropriate.");
        messages.add(systemMessage);
    }
    
    /**
     * Add or replace the system message
     * @param content The content of the system message
     */
    public void addSystemMessage(String content) {
        // Remove any existing system messages
        messages.removeIf(message -> "system".equals(message.get("role")));
        
        // Add the new system message
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", content);
        messages.add(0, systemMessage); // Add at the beginning
    }

    /**
     * Asynchronously categorizes a transaction description using the Deepseek AI API
     * 
     * @param description The transaction description to categorize
     * @param availableCategories Array of available categories to choose from
     * @return A CompletableFuture that will complete with the categorized result
     */
    public static CompletableFuture<String> categorizeTransaction(String description, String[] availableCategories) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Starting transaction categorization for: " + description);
                
                // Step 1: Check for direct category matches (case insensitive)
                for (String category : availableCategories) {
                    if (description.equalsIgnoreCase(category)) {
                        System.out.println("Direct category match found: " + category);
                        return category;
                    }
                }
                
                // Step 2: Try local pattern matching for common transaction types
                // This is faster than API calls and handles most common cases
                String localCategory = matchLocalPatterns(description.toLowerCase(), availableCategories);
                if (localCategory != null) {
                    System.out.println("Local pattern match found: " + localCategory);
                    return localCategory;
                }
                
                // Step 3: Check for context clues (amounts, locations, frequency words)
                String contextCategory = analyzeTransactionContext(description.toLowerCase(), availableCategories);
                if (contextCategory != null) {
                    System.out.println("Context analysis suggests: " + contextCategory);
                    return contextCategory;
                }
                
                // Step 4: Build the category options string for the AI prompt
                StringBuilder categoryOptions = new StringBuilder();
                for (int i = 0; i < availableCategories.length; i++) {
                    categoryOptions.append(availableCategories[i]);
                    if (i < availableCategories.length - 1) {
                        categoryOptions.append(", ");
                    }
                }
                
                System.out.println("Available categories: " + categoryOptions.toString());
                
                // Step 5: Create a more comprehensive prompt with examples for the API
                String prompt = "You are a financial transaction categorizer specialized in detecting complex spending patterns. "
                    + "Categorize the following transaction: '" + description + "' "
                    + "Choose EXACTLY ONE category from this list: " + categoryOptions + ". "
                    + "Consider these examples to guide your decision:\n"
                    + "- 'Monthly rent payment' → Housing\n"
                    + "- 'Grocery shopping at local market' → Food\n"
                    + "- 'Uber ride to airport' → Transportation\n"
                    + "- 'Netflix subscription' → Entertainment\n"
                    + "- 'Red envelope for Chinese New Year' → Chinese New Year\n"
                    + "- 'Utility bill payment' → Utilities\n"
                    + "Respond with ONLY the category name, no explanation or additional text. "
                    + "If unsure, choose the most likely category based on similar real-world transactions.";
                
                System.out.println("Sending enhanced request to DeepSeek API...");
                
                // Make the actual API call
                String result = makeAPICall(prompt);
                System.out.println("Received result from API: " + result);
                
                // Validate and clean up the API response
                String validatedCategory = validateCategory(result, availableCategories);
                System.out.println("Validated category: " + validatedCategory);
                
                return validatedCategory;
            } catch (Exception e) {
                System.err.println("Error during transaction categorization: " + e.getMessage());
                e.printStackTrace();
                // Use more intelligent fallback if possible
                return findBestFallbackCategory(description, availableCategories);
            }
        });
    }
    
    /**
     * Analyzes transaction context for better categorization
     * 
     * @param description The transaction description (lowercase)
     * @param availableCategories Available categories
     * @return Suggested category based on context or null if uncertain
     */
    private static String analyzeTransactionContext(String description, String[] availableCategories) {
        // Check for seasonal contexts
        if (containsAny(description, "new year", "spring festival", "lunar", "red envelope", "hongbao", "festival", "holiday gift")) {
            return findCategory("Chinese New Year", availableCategories);
        }
        
        // Check for recurring payment patterns
        if (containsAny(description, "monthly", "subscription", "recurring", "plan", "membership")) {
            // Determine subscription type
            if (containsAny(description, "netflix", "hulu", "disney", "spotify", "music", "movie", "game", "stream")) {
                return findCategory("Entertainment", availableCategories);
            }
            if (containsAny(description, "gym", "fitness", "workout", "health club")) {
                return findCategory("Personal", availableCategories);
            }
            if (containsAny(description, "insurance", "coverage", "protection")) {
                return findCategory("Insurance", availableCategories);
            }
        }
        
        // Check for transaction locations that provide context
        if (containsAny(description, "restaurant", "café", "cafe", "bar", "dining", "lunch", "dinner", "breakfast")) {
            return findCategory("Food", availableCategories);
        }
        if (containsAny(description, "hotel", "airbnb", "booking.com", "lodging", "accommodation", "resort", "stay")) {
            return findCategory("Travel", availableCategories);
        }
        if (containsAny(description, "hospital", "clinic", "doctor", "dental", "medicine", "pharmacy", "prescription", "medical")) {
            return findCategory("Healthcare", availableCategories);
        }
        
        // No strong context detected
        return null;
    }
    
    /**
     * Checks if a string contains any of the specified keywords
     * 
     * @param text Text to check
     * @param keywords Keywords to look for
     * @return True if any keyword is found
     */
    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find a category from available categories (case insensitive)
     * 
     * @param targetCategory Category to find
     * @param availableCategories Available categories
     * @return The matching category or "Other" if not found
     */
    private static String findCategory(String targetCategory, String[] availableCategories) {
        for (String category : availableCategories) {
            if (category.equalsIgnoreCase(targetCategory)) {
                return category;
            }
        }
        
        // If we have "Other" in categories, use that
        for (String category : availableCategories) {
            if (category.equalsIgnoreCase("Other")) {
                return category;
            }
        }
        
        // Otherwise return the first category (should not happen)
        return availableCategories.length > 0 ? availableCategories[0] : "Other";
    }
    
    /**
     * Finds the best fallback category when AI categorization fails
     * 
     * @param description The transaction description
     * @param availableCategories Available categories
     * @return Best guess category
     */
    private static String findBestFallbackCategory(String description, String[] availableCategories) {
        // Try matching with common patterns first
        String localMatch = matchLocalPatterns(description.toLowerCase(), availableCategories);
        if (localMatch != null) {
            return localMatch;
        }
        
        // If no match found, try basic context analysis
        String descLower = description.toLowerCase();
        
        // Try to match with most common transaction types
        if (descLower.contains("grocery") || descLower.contains("restaurant") || 
                descLower.contains("meal") || descLower.contains("cafe")) {
            return "Food";
        }
        else if (descLower.contains("amazon") || descLower.contains("online") || 
                descLower.contains("purchase") || descLower.contains("buy")) {
            return "Shopping";
        }
        else if (descLower.contains("gym") || descLower.contains("fitness") || 
                descLower.contains("sport") || descLower.contains("equipment")) {
            return "Entertainment";
        }
        else if (descLower.contains("bill") || descLower.contains("water") || 
                descLower.contains("electricity") || descLower.contains("phone")) {
            return "Utilities";
        }
        else if (descLower.contains("rent") || descLower.contains("mortgage") || 
                descLower.contains("lease") || descLower.contains("apartment")) {
            return "Housing";
        }
        else if (descLower.contains("hongbao") || descLower.contains("new year") || 
                descLower.contains("festival") || descLower.contains("spring festival")) {
            return "Chinese New Year";
        }
        
        // Return "Other" as last resort
        return findCategory("Other", availableCategories);
    }
    
    /**
     * Matches transaction description against local patterns
     * 
     * @param description The transaction description (lowercase)
     * @param availableCategories Available categories
     * @return Matched category or null if no match
     */
    private static String matchLocalPatterns(String description, String[] availableCategories) {
        // Map of pattern keywords to categories
        Map<String, String[]> categoryPatterns = new HashMap<>();
        
        // Food patterns
        categoryPatterns.put("Food", new String[]{
            "grocery", "supermarket", "restaurant", "food", "meal", "café", "cafe", "diner", 
            "breakfast", "lunch", "dinner", "snack", "bakery", "coffee", "takeout", "delivery", 
            "饭店", "食品", "餐厅", "超市", "外卖", "美食", "烹饪", "食品杂货"
        });
        
        // Housing patterns
        categoryPatterns.put("Housing", new String[]{
            "rent", "mortgage", "lease", "apartment", "condo", "house", "property", "real estate",
            "housing", "tenant", "landlord", "maintenance", "repair", "home", "residence",
            "房租", "住房", "抵押贷款", "物业费", "房产", "公寓", "住宅", "维修"
        });
        
        // Transportation patterns
        categoryPatterns.put("Transportation", new String[]{
            "gas", "petrol", "fuel", "car", "auto", "vehicle", "parking", "subway", "metro",
            "bus", "train", "taxi", "uber", "lyft", "didi", "transit", "transportation", "toll",
            "汽油", "加油", "停车", "出租车", "公交", "地铁", "交通", "滴滴", "高速费"
        });
        
        // Entertainment patterns
        categoryPatterns.put("Entertainment", new String[]{
            "movie", "cinema", "theater", "concert", "show", "netflix", "hulu", "disney", "spotify",
            "game", "gaming", "playstation", "xbox", "nintendo", "streaming", "subscription", "entertainment",
            "电影", "电影院", "游戏", "演唱会", "表演", "直播", "娱乐", "订阅"
        });
        
        // Utilities patterns
        categoryPatterns.put("Utilities", new String[]{
            "electricity", "water", "gas", "utility", "bill", "power", "energy", "internet",
            "wifi", "broadband", "phone", "mobile", "cable", "tv", "garbage", "waste",
            "电费", "水费", "煤气费", "电话费", "水电", "宽带", "网络", "垃圾处理"
        });
        
        // Shopping patterns
        categoryPatterns.put("Shopping", new String[]{
            "amazon", "taobao", "jd", "tmall", "alibaba", "walmart", "target", "mall", "store",
            "shop", "purchase", "buy", "shopping", "retail", "department", "online", "order",
            "购物", "网购", "商店", "商场", "零售", "买", "购买", "订单"
        });
        
        // Education patterns
        categoryPatterns.put("Education", new String[]{
            "school", "college", "university", "tuition", "course", "class", "education", "student",
            "textbook", "book", "library", "learning", "study", "tutorial", "training", "workshop",
            "学校", "大学", "学费", "课程", "教育", "学生", "书", "学习", "培训"
        });
        
        // Healthcare patterns
        categoryPatterns.put("Healthcare", new String[]{
            "doctor", "hospital", "clinic", "medical", "health", "healthcare", "dental", "dentist",
            "pharmacy", "medicine", "prescription", "therapy", "insurance", "treatment", "emergency",
            "医生", "医院", "诊所", "药店", "医疗", "健康", "牙医", "保险", "治疗"
        });
        
        // Chinese New Year patterns
        categoryPatterns.put("Chinese New Year", new String[]{
            "new year", "chinese new year", "spring festival", "lunar", "red envelope", "hongbao", 
            "festival", "celebration", "holiday", "gift", "tradition", "lucky money", "annual", 
            "春节", "新年", "红包", "过年", "春节", "拜年", "新春", "庆祝", "压岁钱"
        });
        
        // Check each category's patterns
        for (Map.Entry<String, String[]> entry : categoryPatterns.entrySet()) {
            String category = entry.getKey();
            String[] patterns = entry.getValue();
            
            for (String pattern : patterns) {
                if (description.contains(pattern)) {
                    // Check if this category is in available categories
                    return findCategory(category, availableCategories);
                }
            }
        }
        
        // No pattern match found
        return null;
    }
    
    /**
     * Validates that the returned category is in the list of available categories
     * 
     * @param result The category returned by the API
     * @param availableCategories Array of available categories
     * @return The validated category (or "Other" if invalid)
     */
    private static String validateCategory(String result, String[] availableCategories) {
        // Trim the result to handle any extra whitespace
        result = result.trim();
        
        // Check if the result is a valid category
        for (String category : availableCategories) {
            if (category.equalsIgnoreCase(result)) {
                return category; // Return the category with correct casing
            }
        }
        
        // Default to "Other" if the result is not a valid category
        return "Other";
    }

    /**
     * Makes a direct API call for categorization purposes
     * 
     * @param prompt The prompt to send to the API
     * @return The API response
     * @throws IOException If an error occurs during the API call
     */
    private static String makeAPICall(String prompt) throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + DEFAULT_API_KEY);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000); // 10 seconds timeout
        connection.setReadTimeout(30000);    // 30 seconds timeout
        
        // Create single message for categorization
        JSONArray messagesArray = new JSONArray();
        
        // System message for better categorization
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a financial transaction categorizer. Analyze the transaction and respond with only the category name.");
        messagesArray.add(systemMessage);
        
        // User message with the prompt
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messagesArray.add(userMessage);
        
        // Create request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL_CHAT);
        requestBody.put("messages", messagesArray);
        requestBody.put("stream", false);
        requestBody.put("temperature", 0.1); // Low temperature for more consistent results
        requestBody.put("max_tokens", 50); // Short response needed
        
        // Write request body to connection
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Check response code
        int responseCode = connection.getResponseCode();
        
        // Read response
        if (responseCode >= 200 && responseCode < 300) {
            // Success response
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                }
                
                // Parse JSON response to extract just the content
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = (JSONObject) parser.parse(response.toString());
                JSONArray choices = (JSONArray) jsonResponse.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = (JSONObject) choices.get(0);
                    JSONObject message = (JSONObject) firstChoice.get("message");
                    if (message != null) {
                        String content = (String) message.get("content");
                        return content.trim();
                    }
                }
                
                return response.toString();
            } catch (ParseException e) {
                System.err.println("Error parsing API response: " + e.getMessage());
                e.printStackTrace();
                return "Other"; // Default fallback
            }
        } else {
            // Error response
            System.err.println("API error response code: " + responseCode);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String errorLine;
                while ((errorLine = br.readLine()) != null) {
                    response.append(errorLine);
                }
                System.err.println("API error response: " + response.toString());
            }
            
            throw new IOException("API error: " + responseCode);
        }
    }
} 