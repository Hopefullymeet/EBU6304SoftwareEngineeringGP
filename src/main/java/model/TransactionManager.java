package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter CN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    public static void saveTransaction(Transaction transaction) {
        try (FileWriter fw = new FileWriter(TRANSACTIONS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(transaction.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        
        if (!file.exists()) {
            return transactions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    transactions.add(Transaction.fromString(line));
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid transaction: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public static void importFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            int successCount = 0;
            int errorCount = 0;
            
            while ((line = reader.readLine()) != null) {
                try {
                    if (firstLine) {
                        firstLine = false;
                        if (line.contains("Date") || line.contains("日期") ||
                            line.contains("Description") || line.contains("描述") ||
                            line.contains("Category") || line.contains("类别") ||
                            line.contains("Amount") || line.contains("金额")) {
                            continue;
                        }
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        LocalDate date;
                        try {
                            date = LocalDate.parse(parts[0].trim(), CN_DATE_FORMATTER);
                        } catch (Exception e) {
                            try {
                                date = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);
                            } catch (Exception e2) {
                                throw new IllegalArgumentException("无法解析日期: " + parts[0]);
                            }
                        }
                        
                        String description = parts[1].trim();
                        String category = parts[2].trim();
                        double amount;
                        try {
                            amount = Double.parseDouble(parts[3].trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("无法解析金额: " + parts[3]);
                        }
                        
                        Transaction transaction = new Transaction(date, description, category, amount);
                        saveTransaction(transaction);
                        successCount++;
                    } else {
                        throw new IllegalArgumentException("行数据不足: 需要至少4个字段");
                    }
                } catch (Exception e) {
                    System.err.println("跳过无效行: " + line + " (原因: " + e.getMessage() + ")");
                    errorCount++;
                }
            }
            
            System.out.println("导入完成: 成功 " + successCount + " 条记录, 失败 " + errorCount + " 条记录");
            
            if (errorCount > 0) {
                throw new IOException("导入过程中有 " + errorCount + " 条记录失败");
            }
        } catch (IOException e) {
            System.err.println("导入CSV文件时出错: " + e.getMessage());
            throw new RuntimeException("导入CSV文件失败: " + e.getMessage(), e);
        }
    }
    
    public static void exportToCSV(String filePath) {
        List<Transaction> transactions = loadTransactions();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("Date,Description,Category,Amount");
            
            // Write transactions
            for (Transaction transaction : transactions) {
                writer.println(transaction.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 