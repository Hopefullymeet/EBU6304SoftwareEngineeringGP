package model;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UserManager - Manages user authentication, registration and storage.
 * Uses a text file for persisting user data.
 */
public class UserManager {
    
    private static final String USER_DATA_FILE = "users.txt";
    private static UserManager instance;
    private Map<String, User> users;
    private User currentUser;
    
    /**
     * Private constructor for singleton pattern
     */
    private UserManager() {
        users = new HashMap<>();
        loadUsers();
    }
    
    /**
     * Gets the singleton instance of UserManager
     * @return The UserManager instance
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    /**
     * Registers a new user with the provided information
     * @param username The username
     * @param password The plain password (will be encrypted)
     * @param email The email address
     * @return True if registration is successful, false otherwise
     */
    public boolean registerUser(String username, String password, String email) {
        // Check if username already exists
        if (users.containsKey(username)) {
            return false;
        }
        
        // Validate password
        if (!PasswordStrengthChecker.meetsMinimumRequirements(password)) {
            return false;
        }
        
        // Encrypt password
        String encryptedPassword = EncryptionService.encrypt(password);
        if (encryptedPassword == null) {
            return false;
        }
        
        // Create user
        User user = new User(username, encryptedPassword, email);
        users.put(username, user);
        
        // Save user data
        return saveUsers();
    }
    
    /**
     * Authenticates a user with username and password
     * @param username The username
     * @param password The plain password
     * @return True if authentication is successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        
        // Decrypt and verify password
        String decryptedPassword = EncryptionService.decrypt(user.getEncryptedPassword());
        if (decryptedPassword == null || !decryptedPassword.equals(password)) {
            return false;
        }
        
        // Set current user
        currentUser = user;
        return true;
    }
    
    /**
     * Gets the current authenticated user
     * @return The current user or null if not authenticated
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Updates the current user's timeout setting
     * @param minutes The timeout in minutes
     * @return True if update is successful, false otherwise
     */
    public boolean updateSessionTimeout(int minutes) {
        if (currentUser == null || minutes < 1) {
            return false;
        }
        
        currentUser.setSessionTimeoutMinutes(minutes);
        return saveUsers();
    }
    
    /**
     * Updates the current user with new preferences
     * @param user The updated user object
     * @return True if update is successful, false otherwise
     */
    public boolean updateCurrentUser(User user) {
        if (currentUser == null || user == null) {
            return false;
        }
        
        // Update the user in the map
        users.put(user.getUsername(), user);
        
        // Update current user reference
        currentUser = user;
        
        // Save changes to file
        return saveUsers();
    }
    
    /**
     * Changes the current user's password
     * @param currentPassword The current password for verification
     * @param newPassword The new password to set
     * @return True if password change is successful, false otherwise
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        // Check if user is logged in
        if (currentUser == null) {
            return false;
        }
        
        // Verify current password
        String decryptedPassword = EncryptionService.decrypt(currentUser.getEncryptedPassword());
        if (decryptedPassword == null || !decryptedPassword.equals(currentPassword)) {
            return false;
        }
        
        // Validate new password
        if (!PasswordStrengthChecker.meetsMinimumRequirements(newPassword)) {
            return false;
        }
        
        // Encrypt and set new password
        String encryptedNewPassword = EncryptionService.encrypt(newPassword);
        if (encryptedNewPassword == null) {
            return false;
        }
        
        // Update user's password
        currentUser.setEncryptedPassword(encryptedNewPassword);
        
        // Save changes
        return saveUsers();
    }
    
    /**
     * Loads users from the data file
     */
    private void loadUsers() {
        try {
            Path path = Paths.get(USER_DATA_FILE);
            
            // Create file if it doesn't exist
            if (!Files.exists(path)) {
                Files.createFile(path);
                return;
            }
            
            // Read users from file
            BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    User user = User.fromFileString(line);
                    users.put(user.getUsername(), user);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Saves users to the data file
     * @return True if save is successful, false otherwise
     */
    private boolean saveUsers() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE));
            for (User user : users.values()) {
                writer.write(user.toFileString());
                writer.newLine();
            }
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
} 
