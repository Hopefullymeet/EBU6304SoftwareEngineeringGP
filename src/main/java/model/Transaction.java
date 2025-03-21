package model;

import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String description;
    private String category;
    private double amount;
    
    public Transaction(LocalDate date, String description, String category, double amount) {
        this.date = date;
        this.description = description;
        this.category = category;
        this.amount = amount;
    }
    
    // Getters
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    
    // Setters
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%.2f", 
            date.toString(), description, category, amount);
    }
    
    public static Transaction fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid transaction format");
        }
        return new Transaction(
            LocalDate.parse(parts[0]),
            parts[1],
            parts[2],
            Double.parseDouble(parts[3])
        );
    }
} 