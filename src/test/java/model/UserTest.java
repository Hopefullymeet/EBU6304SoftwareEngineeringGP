package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User class
 */
public class UserTest {

    @Test
    @DisplayName("Test User constructor")
    public void testUserConstructor() {
        // Prepare test data
        String username = "testUser";
        String encryptedPassword = "encryptedPass123";
        String email = "test@example.com";
        
        // Execute test
        User user = new User(username, encryptedPassword, email);
        
        // Verify results
        assertEquals(username, user.getUsername(), "Username should match the input username");
        assertEquals(encryptedPassword, user.getEncryptedPassword(), "Encrypted password should match the input encrypted password");
        assertEquals(email, user.getEmail(), "Email should match the input email");
        
        // Verify default values
        assertEquals(1, user.getSessionTimeoutMinutes(), "Default session timeout should be 1 minute");
        assertEquals("English", user.getLanguage(), "Default language should be English");
        assertEquals("¥ CNY (Chinese Yuan)", user.getCurrency(), "Default currency should be CNY");
        assertEquals("Light", user.getTheme(), "Default theme should be Light");
        assertTrue(user.isTransactionAlerts(), "Transaction alerts should be enabled by default");
        assertTrue(user.isBudgetAlerts(), "Budget alerts should be enabled by default");
        assertTrue(user.isBillReminders(), "Bill reminders should be enabled by default");
        assertTrue(user.isFinancialTips(), "Financial tips should be enabled by default");
        assertTrue(user.isAllowDataAnalytics(), "Data analytics should be allowed by default");
        assertTrue(user.isShareAnonymousData(), "Sharing anonymous data should be enabled by default");
    }
    
    @Test
    @DisplayName("Test User setter methods")
    public void testSetters() {
        // Prepare test data
        User user = new User("initial", "password", "initial@example.com");
        
        // Execute test
        user.setUsername("updated");
        user.setEncryptedPassword("newPassword");
        user.setEmail("updated@example.com");
        user.setSessionTimeoutMinutes(5);
        user.setLanguage("中文 (Chinese)");
        user.setCurrency("$ USD (US Dollar)");
        user.setTheme("Dark");
        user.setTransactionAlerts(false);
        user.setBudgetAlerts(false);
        user.setBillReminders(false);
        user.setFinancialTips(false);
        user.setAllowDataAnalytics(false);
        user.setShareAnonymousData(false);
        
        // Verify results
        assertEquals("updated", user.getUsername(), "Username should be updated");
        assertEquals("newPassword", user.getEncryptedPassword(), "Encrypted password should be updated");
        assertEquals("updated@example.com", user.getEmail(), "Email should be updated");
        assertEquals(5, user.getSessionTimeoutMinutes(), "Session timeout should be updated to 5 minutes");
        assertEquals("中文 (Chinese)", user.getLanguage(), "Language should be updated");
        assertEquals("$ USD (US Dollar)", user.getCurrency(), "Currency should be updated");
        assertEquals("Dark", user.getTheme(), "Theme should be updated");
        assertFalse(user.isTransactionAlerts(), "Transaction alerts should be disabled");
        assertFalse(user.isBudgetAlerts(), "Budget alerts should be disabled");
        assertFalse(user.isBillReminders(), "Bill reminders should be disabled");
        assertFalse(user.isFinancialTips(), "Financial tips should be disabled");
        assertFalse(user.isAllowDataAnalytics(), "Data analytics should be disabled");
        assertFalse(user.isShareAnonymousData(), "Sharing anonymous data should be disabled");
    }
    
    @Test
    @DisplayName("Test converting User object to file string")
    public void testToFileString() {
        // Prepare test data - using default values
        User user = new User("testUser", "encryptedPass123", "test@example.com");
        
        // Execute test
        String result = user.toFileString();
        
        // Verify results
        String expected = "testUser|encryptedPass123|test@example.com|1|English|¥ CNY (Chinese Yuan)|Light|" + 
                         "true|true|true|true|true|true";
        assertEquals(expected, result, "toFileString should return a correctly formatted string");
    }
    
    @Test
    @DisplayName("Test converting User object to file string - custom values")
    public void testToFileStringWithCustomValues() {
        // Prepare test data - using custom values
        User user = new User("testUser", "encryptedPass123", "test@example.com");
        user.setSessionTimeoutMinutes(5);
        user.setLanguage("中文 (Chinese)");
        user.setCurrency("$ USD (US Dollar)");
        user.setTheme("Dark");
        user.setTransactionAlerts(false);
        user.setBudgetAlerts(false);
        
        // Execute test
        String result = user.toFileString();
        
        // Verify results
        String expected = "testUser|encryptedPass123|test@example.com|5|中文 (Chinese)|$ USD (US Dollar)|Dark|" + 
                         "false|false|true|true|true|true";
        assertEquals(expected, result, "toFileString should return a correctly formatted string with custom values");
    }
    
    @Test
    @DisplayName("Create User object from file string - complete data")
    public void testFromFileStringWithCompleteData() {
        // Prepare test data
        String fileString = "testUser|encryptedPass123|test@example.com|5|中文 (Chinese)|$ USD (US Dollar)|Dark|" + 
                          "false|false|true|false|true|false";
        
        // Execute test
        User user = User.fromFileString(fileString);
        
        // Verify results
        assertEquals("testUser", user.getUsername(), "Username should be correctly parsed");
        assertEquals("encryptedPass123", user.getEncryptedPassword(), "Encrypted password should be correctly parsed");
        assertEquals("test@example.com", user.getEmail(), "Email should be correctly parsed");
        assertEquals(5, user.getSessionTimeoutMinutes(), "Session timeout should be correctly parsed");
        assertEquals("中文 (Chinese)", user.getLanguage(), "Language should be correctly parsed");
        assertEquals("$ USD (US Dollar)", user.getCurrency(), "Currency should be correctly parsed");
        assertEquals("Dark", user.getTheme(), "Theme should be correctly parsed");
        assertFalse(user.isTransactionAlerts(), "Transaction alerts setting should be correctly parsed");
        assertFalse(user.isBudgetAlerts(), "Budget alerts setting should be correctly parsed");
        assertTrue(user.isBillReminders(), "Bill reminders setting should be correctly parsed");
        assertFalse(user.isFinancialTips(), "Financial tips setting should be correctly parsed");
        assertTrue(user.isAllowDataAnalytics(), "Data analytics setting should be correctly parsed");
        assertFalse(user.isShareAnonymousData(), "Share anonymous data setting should be correctly parsed");
    }
    
    @Test
    @DisplayName("Create User object from file string - basic data only")
    public void testFromFileStringWithBasicData() {
        // Prepare test data - only basic information
        String fileString = "testUser|encryptedPass123|test@example.com";
        
        // Execute test
        User user = User.fromFileString(fileString);
        
        // Verify results - basic information correctly parsed
        assertEquals("testUser", user.getUsername(), "Username should be correctly parsed");
        assertEquals("encryptedPass123", user.getEncryptedPassword(), "Encrypted password should be correctly parsed");
        assertEquals("test@example.com", user.getEmail(), "Email should be correctly parsed");
        
        // Verify other fields use default values
        assertEquals(1, user.getSessionTimeoutMinutes(), "Session timeout should use default value of 1 minute");
        assertEquals("English", user.getLanguage(), "Language should use default value of English");
        assertEquals("¥ CNY (Chinese Yuan)", user.getCurrency(), "Currency should use default value of CNY");
        assertEquals("Light", user.getTheme(), "Theme should use default value of Light");
        assertTrue(user.isTransactionAlerts(), "Transaction alerts should use default value of true");
        assertTrue(user.isBudgetAlerts(), "Budget alerts should use default value of true");
        assertTrue(user.isBillReminders(), "Bill reminders should use default value of true");
        assertTrue(user.isFinancialTips(), "Financial tips should use default value of true");
        assertTrue(user.isAllowDataAnalytics(), "Data analytics should use default value of true");
        assertTrue(user.isShareAnonymousData(), "Share anonymous data should use default value of true");
    }
    
    @Test
    @DisplayName("Create User object from file string - handle invalid timeout value")
    public void testFromFileStringWithInvalidTimeout() {
        // Prepare test data - invalid session timeout value
        String fileString = "testUser|encryptedPass123|test@example.com|invalid|English";
        
        // Execute test
        User user = User.fromFileString(fileString);
        
        // Verify results - should use default timeout value
        assertEquals(1, user.getSessionTimeoutMinutes(), "Invalid session timeout value should default to 1 minute");
        assertEquals("English", user.getLanguage(), "Language should be correctly parsed");
    }
} 