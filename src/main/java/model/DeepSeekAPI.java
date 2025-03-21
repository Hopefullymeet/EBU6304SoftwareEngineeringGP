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
                
                // Check if the description directly matches a category name first (case insensitive)
                for (String category : availableCategories) {
                    if (description.equalsIgnoreCase(category)) {
                        System.out.println("Direct category match found: " + category);
                        return category;
                    }
                }
                
                // Build the category options string
                StringBuilder categoryOptions = new StringBuilder();
                for (int i = 0; i < availableCategories.length; i++) {
                    categoryOptions.append(availableCategories[i]);
                    if (i < availableCategories.length - 1) {
                        categoryOptions.append(", ");
                    }
                }
                
                System.out.println("Available categories: " + categoryOptions.toString());
                
                // Create the prompt for the API
                String prompt = "You are a financial transaction categorizer. "
                    + "Categorize the following transaction: '" + description + "' "
                    + "You MUST choose EXACTLY ONE category from this list: " + categoryOptions + ". "
                    + "Respond with ONLY the category name, no explanation or additional text. "
                    + "If unsure, choose the most likely category based on the description.";
                
                System.out.println("Sending request to DeepSeek API...");
                
                // Make the actual API call
                String result = makeAPICall(prompt);
                System.out.println("Received result from API: " + result);
                
                String validatedCategory = validateCategory(result, availableCategories);
                System.out.println("Validated category: " + validatedCategory);
                
                return validatedCategory;
            } catch (Exception e) {
                System.err.println("Error during transaction categorization: " + e.getMessage());
                e.printStackTrace();
                return "Other"; // Default fallback category
            }
        });
    }
    
    /**
     * Makes the actual API call to Deepseek
     * 
     * @param prompt The prompt to send to the API
     * @return The API response
     */
    private static String makeAPICall(String prompt) {
        try {
            System.out.println("Preparing DeepSeek API request with key: " + DEFAULT_API_KEY.substring(0, 5) + "...");
            
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + DEFAULT_API_KEY);
            connection.setConnectTimeout(10000); // 10 seconds timeout for connection
            connection.setReadTimeout(30000);    // 30 seconds timeout for reading
            connection.setDoOutput(true);
            
            // Create request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL_CHAT);
            
            // Create messages array with system and user messages
            JSONArray messagesArray = new JSONArray();
            
            // Add system message defining the task
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a financial transaction categorizer. Your task is to categorize financial transactions into exactly one of the provided categories.");
            messagesArray.add(systemMessage);
            
            // Add user message with the prompt
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messagesArray.add(userMessage);
            
            requestBody.put("messages", messagesArray);
            requestBody.put("temperature", 0.1); // Low temperature for more deterministic responses
            requestBody.put("max_tokens", 10);   // We only need a short response - just the category name
            
            String requestBodyJson = requestBody.toJSONString();
            System.out.println("Request body: " + requestBodyJson);
            
            // Write request body to connection
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBodyJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            System.out.println("API response code: " + responseCode);
            
            if (responseCode >= 200 && responseCode < 300) {
                // Success response
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    
                    String fullResponse = response.toString();
                    System.out.println("API full response: " + fullResponse);
                    
                    // Parse the JSON response to extract the AI's answer
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject jsonResponse = (JSONObject) parser.parse(fullResponse);
                        JSONArray choices = (JSONArray) jsonResponse.get("choices");
                        
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject firstChoice = (JSONObject) choices.get(0);
                            JSONObject message = (JSONObject) firstChoice.get("message");
                            
                            if (message != null && message.containsKey("content")) {
                                String content = ((String) message.get("content")).trim();
                                System.out.println("Extracted content: " + content);
                                return content;
                            } else {
                                System.err.println("Message does not contain content field");
                            }
                        } else {
                            System.err.println("No choices in API response");
                        }
                    } catch (ParseException e) {
                        System.err.println("Error parsing API response: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                // Error response
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine.trim());
                    }
                    System.err.println("API error response: " + errorResponse.toString());
                }
                
                // If the API call fails, fall back to the simulation
                System.out.println("Falling back to simulation due to API error");
                return simulateAIResponse(prompt);
            }
            
            // If we couldn't parse the response properly, fall back to simulation
            System.out.println("Falling back to simulation due to parsing issues");
            return simulateAIResponse(prompt);
            
        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            e.printStackTrace();
            
            // If an exception occurs, fall back to the simulation
            System.out.println("Falling back to simulation due to exception");
            return simulateAIResponse(prompt);
        }
    }
    
    /**
     * Simulates an AI response for demonstration purposes
     * In a real implementation, this would be replaced with actual API response parsing
     * 
     * @param prompt The prompt sent to the API
     * @return A simulated AI response
     */
    private static String simulateAIResponse(String prompt) {
        String description = prompt.substring(
            prompt.indexOf("'") + 1, 
            prompt.lastIndexOf("'")
        ).toLowerCase();
        
        // Simple keyword matching for demonstration
        if (description.contains("school") || description.contains("book") || 
            description.contains("tuition") || description.contains("class")) {
            return "Education";
        } 
        else if (description.contains("doctor") || description.contains("hospital") || 
                description.contains("medicine") || description.contains("pharmacy")) {
            return "Medical";
        }
        else if (description.contains("hotel") || description.contains("flight") || 
                description.contains("vacation") || description.contains("trip")) {
            return "Travel";
        }
        else if (description.contains("grocery") || description.contains("restaurant") || 
                description.contains("meal") || description.contains("cafe")) {
            return "Food";
        }
        else if (description.contains("amazon") || description.contains("online") || 
                description.contains("purchase") || description.contains("buy")) {
            return "Shopping";
        }
        else if (description.contains("gym") || description.contains("fitness") || 
                description.contains("sport") || description.contains("equipment")) {
            return "Entertainment";
        }
        else if (description.contains("bill") || description.contains("water") || 
                description.contains("electricity") || description.contains("phone")) {
            return "Utilities";
        }
        else if (description.contains("rent") || description.contains("mortgage") || 
                description.contains("lease") || description.contains("apartment")) {
            return "Housing";
        }
        else if (description.contains("hongbao") || description.contains("new year") || 
                description.contains("festival") || description.contains("spring festival")) {
            return "Chinese New Year";
        }
        else {
            // Return "Other" as a fallback
            return "Other";
        }
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
} 