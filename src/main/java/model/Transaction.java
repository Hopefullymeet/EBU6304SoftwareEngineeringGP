package model;

import java.time.LocalDate;

public class Transaction {
    private String userId;
    private LocalDate date;
    private String description;
    private String category;
    private double amount;
    
    public Transaction(LocalDate date, String description, String category, double amount) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
        // Default userId to empty string when not specified
        this.userId = "";
    }
    
    public Transaction(String userId, LocalDate date, String description, String category, double amount) {
        this.userId = userId;
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }
    
    // Getters
    public String getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    
    // Setters
    public void setUserId(String userId) { this.userId = userId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%.2f", 
            userId, date.toString(), description, category, amount);
    }
    
    public static Transaction fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid transaction format");
        }
        
        if (parts.length == 4) {
            // Old format without userId - backward compatibility
            return new Transaction(
                LocalDate.parse(parts[0]),
                parts[1],
                parts[2],
                Double.parseDouble(parts[3])
            );
        } else {
            // New format with userId
            return new Transaction(
                parts[0],
                LocalDate.parse(parts[1]),
                parts[2],
                parts[3],
                Double.parseDouble(parts[4])
            );
        }
    }
} 