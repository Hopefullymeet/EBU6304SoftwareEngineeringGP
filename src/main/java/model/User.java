package model;

/**
 * User - Model class for user data.
 * Stores basic user information like username, encrypted password and email.
 */
public class User {
    private String username;
    private String encryptedPassword;
    private String email;
    private int sessionTimeoutMinutes = 1; // Default timeout is 1 minute
    
    // User preferences
    private String language = "English"; // Default language
    private String currency = "¥ CNY (Chinese Yuan)"; // Default currency
    private String theme = "Light"; // Default theme
    private boolean transactionAlerts = true;
    private boolean budgetAlerts = true;
    private boolean billReminders = true;
    private boolean financialTips = true;
    private boolean allowDataAnalytics = true;
    private boolean shareAnonymousData = true;

    /**
     * Constructor for User
     * @param username The username
     * @param encryptedPassword The encrypted password
     * @param email The user's email
     */
    public User(String username, String encryptedPassword, String email) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.email = email;
    }

    // Getters and setters
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }
    
    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }
    
    // Getters and setters for user preferences
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public boolean isTransactionAlerts() {
        return transactionAlerts;
    }
    
    public void setTransactionAlerts(boolean transactionAlerts) {
        this.transactionAlerts = transactionAlerts;
    }
    
    public boolean isBudgetAlerts() {
        return budgetAlerts;
    }
    
    public void setBudgetAlerts(boolean budgetAlerts) {
        this.budgetAlerts = budgetAlerts;
    }
    
    public boolean isBillReminders() {
        return billReminders;
    }
    
    public void setBillReminders(boolean billReminders) {
        this.billReminders = billReminders;
    }
    
    public boolean isFinancialTips() {
        return financialTips;
    }
    
    public void setFinancialTips(boolean financialTips) {
        this.financialTips = financialTips;
    }
    
    public boolean isAllowDataAnalytics() {
        return allowDataAnalytics;
    }
    
    public void setAllowDataAnalytics(boolean allowDataAnalytics) {
        this.allowDataAnalytics = allowDataAnalytics;
    }
    
    public boolean isShareAnonymousData() {
        return shareAnonymousData;
    }
    
    public void setShareAnonymousData(boolean shareAnonymousData) {
        this.shareAnonymousData = shareAnonymousData;
    }
    
    /**
     * Converts the user object to a string format for storage in a text file
     * Format: username|encryptedPassword|email|sessionTimeoutMinutes|language|currency|theme|alerts...
     * @return Formatted string representation of the user
     */
    public String toFileString() {
        return username + "|" + encryptedPassword + "|" + email + "|" + sessionTimeoutMinutes + "|" +
               language + "|" + currency + "|" + theme + "|" + 
               transactionAlerts + "|" + budgetAlerts + "|" + billReminders + "|" + 
               financialTips + "|" + allowDataAnalytics + "|" + shareAnonymousData;
    }
    
    /**
     * Creates a User object from a formatted string from storage
     * @param fileString The formatted string from the file
     * @return A new User object
     */
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        User user = new User(parts[0], parts[1], parts[2]);
        
        // Parse basic user information
        if (parts.length > 3) {
            try {
                user.setSessionTimeoutMinutes(Integer.parseInt(parts[3]));
            } catch (NumberFormatException e) {
                // Use default if parsing fails
                user.setSessionTimeoutMinutes(1);
            }
        }
        
        // Parse user preferences
        if (parts.length > 4) user.setLanguage(parts[4]);
        if (parts.length > 5) user.setCurrency(parts[5]);
        if (parts.length > 6) user.setTheme(parts[6]);
        
        // Parse boolean preferences
        if (parts.length > 7) user.setTransactionAlerts(Boolean.parseBoolean(parts[7]));
        if (parts.length > 8) user.setBudgetAlerts(Boolean.parseBoolean(parts[8]));
        if (parts.length > 9) user.setBillReminders(Boolean.parseBoolean(parts[9]));
        if (parts.length > 10) user.setFinancialTips(Boolean.parseBoolean(parts[10]));
        if (parts.length > 11) user.setAllowDataAnalytics(Boolean.parseBoolean(parts[11]));
        if (parts.length > 12) user.setShareAnonymousData(Boolean.parseBoolean(parts[12]));
        
        return user;
    }
} 