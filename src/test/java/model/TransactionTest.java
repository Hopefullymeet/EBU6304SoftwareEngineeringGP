package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Tests for Transaction class functionality
 */
public class TransactionTest {

    @Test
    @DisplayName("Test Transaction constructor without userId")
    public void testTransactionConstructorWithoutUserId() {
        // Prepare test data
        LocalDate date = LocalDate.of(2023, 1, 15);
        String description = "Groceries";
        String category = "Food";
        double amount = 123.45;
        
        // Execute test
        Transaction transaction = new Transaction(date, description, category, amount);
        
        // Verify results
        assertEquals(date, transaction.getDate(), "Date should match the input date");
        assertEquals(description, transaction.getDescription(), "Description should match the input description");
        assertEquals(category, transaction.getCategory(), "Category should match the input category");
        assertEquals(amount, transaction.getAmount(), 0.001, "Amount should match the input amount");
        assertEquals("", transaction.getUserId(), "UserId should default to empty string when not provided");
    }
    
    @Test
    @DisplayName("Test Transaction constructor with userId")
    public void testTransactionConstructorWithUserId() {
        // Prepare test data
        String userId = "user123";
        LocalDate date = LocalDate.of(2023, 1, 15);
        String description = "Groceries";
        String category = "Food";
        double amount = 123.45;
        
        // Execute test
        Transaction transaction = new Transaction(userId, date, description, category, amount);
        
        // Verify results
        assertEquals(userId, transaction.getUserId(), "UserId should match the input userId");
        assertEquals(date, transaction.getDate(), "Date should match the input date");
        assertEquals(description, transaction.getDescription(), "Description should match the input description");
        assertEquals(category, transaction.getCategory(), "Category should match the input category");
        assertEquals(amount, transaction.getAmount(), 0.001, "Amount should match the input amount");
    }
    
    @Test
    @DisplayName("Test Transaction toString method")
    public void testToString() {
        // Prepare test data
        String userId = "user123";
        LocalDate date = LocalDate.of(2023, 1, 15);
        String description = "Groceries";
        String category = "Food";
        double amount = 123.45;
        
        Transaction transaction = new Transaction(userId, date, description, category, amount);
        
        // Execute test
        String result = transaction.toString();
        
        // Verify results
        String expected = "user123,2023-01-15,Groceries,Food,123.45";
        assertEquals(expected, result, "toString method should return a string in the specified format");
    }
    
    @Test
    @DisplayName("Create Transaction from string (old format, without userId)")
    public void testFromStringWithoutUserId() {
        // Prepare test data
        String transactionStr = "2023-01-15,Groceries,Food,123.45";
        
        // Execute test
        Transaction transaction = Transaction.fromString(transactionStr);
        
        // Verify results
        assertEquals(LocalDate.of(2023, 1, 15), transaction.getDate(), "Date should be correctly parsed");
        assertEquals("Groceries", transaction.getDescription(), "Description should be correctly parsed");
        assertEquals("Food", transaction.getCategory(), "Category should be correctly parsed");
        assertEquals(123.45, transaction.getAmount(), 0.001, "Amount should be correctly parsed");
        assertEquals("", transaction.getUserId(), "UserId should default to empty string when not provided");
    }
    
    @Test
    @DisplayName("Create Transaction from string (new format, with userId)")
    public void testFromStringWithUserId() {
        // Prepare test data
        String transactionStr = "user123,2023-01-15,Groceries,Food,123.45";
        
        // Execute test
        Transaction transaction = Transaction.fromString(transactionStr);
        
        // Verify results
        assertEquals("user123", transaction.getUserId(), "UserId should be correctly parsed");
        assertEquals(LocalDate.of(2023, 1, 15), transaction.getDate(), "Date should be correctly parsed");
        assertEquals("Groceries", transaction.getDescription(), "Description should be correctly parsed");
        assertEquals("Food", transaction.getCategory(), "Category should be correctly parsed");
        assertEquals(123.45, transaction.getAmount(), 0.001, "Amount should be correctly parsed");
    }
    
    @Test
    @DisplayName("Test creating Transaction from invalid string should throw exception")
    public void testFromStringInvalid() {
        // Prepare test data
        String invalidStr = "Not,Enough,Parts";
        
        // Execute test and verify results
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.fromString(invalidStr);
        }, "Should throw IllegalArgumentException when passed an invalid string");
    }
    
    @Test
    @DisplayName("Test Transaction setter methods")
    public void testSetters() {
        // Prepare test data
        Transaction transaction = new Transaction(LocalDate.of(2023, 1, 1), "Initial", "Other", 0.0);
        
        // Execute test
        transaction.setUserId("newUser");
        transaction.setDate(LocalDate.of(2023, 2, 2));
        transaction.setDescription("Updated Description");
        transaction.setCategory("Updated Category");
        transaction.setAmount(999.99);
        
        // Verify results
        assertEquals("newUser", transaction.getUserId(), "UserId should be updated");
        assertEquals(LocalDate.of(2023, 2, 2), transaction.getDate(), "Date should be updated");
        assertEquals("Updated Description", transaction.getDescription(), "Description should be updated");
        assertEquals("Updated Category", transaction.getCategory(), "Category should be updated");
        assertEquals(999.99, transaction.getAmount(), 0.001, "Amount should be updated");
    }
} 