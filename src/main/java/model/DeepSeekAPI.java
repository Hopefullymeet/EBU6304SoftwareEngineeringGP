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

/**
 * DeepSeekAPI - Client for interacting with the DeepSeek Chat API.
 * This class handles sending messages to the DeepSeek AI and processing responses.
 */
public class DeepSeekAPI {
    
    // Available DeepSeek models
    public static final String MODEL_CHAT = "deepseek-chat";
    public static final String MODEL_REASONER = "deepseek-reasoner";
    
    // API Configuration
    private final String BASE_URL = "https://api.deepseek.com/chat/completions";
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
        systemMessage.put("content", "You are a helpful financial advisor assistant. Provide concise, practical advice on personal finance, investments, budgeting, and savings. Use clear language and focus on actionable tips that can help users improve their financial situation. If possible, tailor your advice to the Chinese financial context when appropriate.");
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
        
        // Create a StringBuilder to collect the full response
        final StringBuilder fullResponse = new StringBuilder();
        
        // Create a SwingWorker to handle API call in background
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                makeStreamingApiCall(chunk -> {
                    // Process each chunk
                    try {
                        if (chunk != null && !chunk.isEmpty()) {
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
                    }
                });
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
                } catch (Exception e) {
                    e.printStackTrace();
                    responseCallback.accept("\nError: " + e.getMessage());
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
        
        // Write request body to connection
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // Read streaming response line by line
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
                }
            }
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
} 