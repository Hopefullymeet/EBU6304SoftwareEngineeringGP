package model;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TransactionManager {
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter CN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    // Additional date formats for better bank compatibility
    private static final DateTimeFormatter[] SUPPORTED_DATE_FORMATS = {
        DATE_FORMATTER,                                      // yyyy-MM-dd
        CN_DATE_FORMATTER,                                   // yyyy/MM/dd
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),           // MM/dd/yyyy
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),           // dd/MM/yyyy
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),           // yyyy.MM.dd
        DateTimeFormatter.ofPattern("MMM dd, yyyy"),         // Jan 01, 2023
        DateTimeFormatter.ofPattern("dd-MMM-yyyy")           // 01-Jan-2023
    };
    
    // CSV delimiter patterns
    private static final String[] POSSIBLE_DELIMITERS = {",", ";", "\t", "\\|"};
    
    public static void saveTransaction(Transaction transaction) {
        try (FileWriter fw = new FileWriter(TRANSACTIONS_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            // Ensure transaction has the current user's ID if not set
            if (transaction.getUserId().isEmpty()) {
                User currentUser = UserManager.getInstance().getCurrentUser();
                if (currentUser != null) {
                    transaction.setUserId(currentUser.getUsername());
                }
            }
            
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
        
        // Get current user
        User currentUser = UserManager.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUsername() : "";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Transaction transaction = Transaction.fromString(line);
                    
                    // Only add transactions belonging to the current user or with empty userId (old format)
                    if (currentUser != null && 
                        (transaction.getUserId().equals(currentUserId) || transaction.getUserId().isEmpty())) {
                        // If transaction has empty userId, update it
                        if (transaction.getUserId().isEmpty()) {
                            transaction.setUserId(currentUserId);
                        }
                        transactions.add(transaction);
                    } else if (currentUser == null && transaction.getUserId().isEmpty()) {
                        // If no user is logged in, only show transactions with empty userId
                        transactions.add(transaction);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid transaction: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Enhanced CSV import with better error handling and bank compatibility
     * 
     * @param filePath Path to the CSV file
     * @throws ImportException If errors occur during import
     */
    public static void importFromCSV(String filePath) throws ImportException {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Map<Integer, String> lineErrors = new HashMap<>();
            int successCount = 0;
            int errorCount = 0;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new ImportException("File does not exist: " + filePath);
        }
        if (!filePath.toLowerCase().endsWith(".csv")) {
            warnings.add("File does not have .csv extension. Import may fail if format is incorrect.");
        }

        try {
            CsvFormat csvFormat = detectCsvFormat(filePath);
            if (csvFormat == null) {
                throw new ImportException("Could not detect CSV format. Please ensure the file is a valid CSV.");
            }
            if (!csvFormat.hasHeaders) {
                warnings.add("No headers detected in CSV file. Assuming first row contains data.");
            }

            // Get current user
            User currentUser = UserManager.getInstance().getCurrentUser();
            String currentUserId = currentUser != null ? currentUser.getUsername() : "";

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;
                boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.trim().isEmpty()) continue;

                try {
                        if (firstLine && csvFormat.hasHeaders) {
                        firstLine = false;
                            continue;
                        }
                        firstLine = false;

                        // Use robust CSV line parser instead of simple split
                        List<String> parts = parseCsvLine(line, csvFormat.delimiter);

                        if (parts.size() < 4) {
                            throw new IllegalArgumentException("Insufficient data columns: Found " + parts.size() + ", need at least 4 (Date, Description, Category, Amount). Original line: " + line);
                        }

                        int dateIndex = csvFormat.dateColumn;
                        int descIndex = csvFormat.descriptionColumn;
                        int catIndex = csvFormat.categoryColumn;
                        int amountIndex = csvFormat.amountColumn;
                        
                        // Check indices are within range based on parsed parts
                        if (dateIndex >= parts.size() || descIndex >= parts.size() || 
                            catIndex >= parts.size() || amountIndex >= parts.size()) {
                            throw new IllegalArgumentException("Detected column index out of range for parsed data. Date:" + dateIndex + ", Desc:" + descIndex + ", Cat:" + catIndex + ", Amt:" + amountIndex + ". Parsed Columns: " + parts.size());
                            }

                        // Parse date
                        LocalDate date = parseDate(parts.get(dateIndex));
                        
                        // Get description and category
                        String description = parts.get(descIndex);
                        String category = parts.get(catIndex);
                        
                        // Parse amount
                        double amount = parseAmount(parts.get(amountIndex));
                        
                        // Create transaction with user ID
                        Transaction transaction = new Transaction(currentUserId, date, description, category, amount);
                        saveTransaction(transaction);
                        successCount++;

                    } catch (Exception e) {
                        errorCount++;
                        String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
                        lineErrors.put(lineNumber, errorMsg);
                        System.err.println("Error processing line " + lineNumber + ": " + line + " (Reason: " + errorMsg + ")");
                         // Log stack trace for debugging unexpected errors
                        if (!(e instanceof IllegalArgumentException || e instanceof DateTimeParseException || e instanceof NumberFormatException)) {
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("Import completed: " + successCount + " records successful, " + errorCount + " records failed");

                if (errorCount > 0) {
                    throw buildImportException(successCount, errorCount, warnings, lineErrors);
                }

            } catch (IOException e) {
                throw new ImportException("Error reading CSV file: " + e.getMessage(), e);
            }

        } catch (ImportException e) {
            throw e;
                } catch (Exception e) {
            e.printStackTrace(); // Log unexpected errors
            throw new ImportException("Unexpected error during import process: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parses a single line of CSV text, respecting quotes.
     *
     * @param line The line to parse.
     * @param delimiter The delimiter character.
     * @return A list of fields.
     */
    private static List<String> parseCsvLine(String line, String delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        char delimiterChar = delimiter.charAt(0); // Assuming single char delimiter

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Handle quotes: could be start/end of quoted field or escaped quote
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote ""
                    currentField.append('"');
                    i++; // Skip the second quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == delimiterChar && !inQuotes) {
                // Delimiter found outside quotes, finalize current field
                fields.add(currentField.toString().trim());
                currentField.setLength(0); // Reset for next field
            } else {
                // Regular character, append to current field
                currentField.append(c);
            }
        }
        
        // Add the last field
        fields.add(currentField.toString().trim());

        return fields;
    }
    
    /**
     * Parses the date string using supported formats.
     */
    private static LocalDate parseDate(String dateStr) throws DateTimeParseException {
        dateStr = dateStr.trim();
        DateTimeParseException lastException = null;
        for (DateTimeFormatter formatter : SUPPORTED_DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                lastException = e;
            }
        }
        throw new DateTimeParseException("Cannot parse date: " + dateStr +
            ". Supported formats include: yyyy-MM-dd, yyyy/MM/dd, MM/dd/yyyy, etc.", dateStr, 0, lastException);
    }

    /**
     * Parses the amount string, removing common currency symbols and thousands separators.
     */
    private static double parseAmount(String amountStr) throws NumberFormatException {
        amountStr = amountStr.trim()
                           .replace("\"", "") // Remove quotes
                           .replace("'", "")  // Remove single quotes
                           .replaceAll("[$,¥€£]", "") // Remove currency symbols
                           .replace(",", ""); // Remove thousands separators
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Cannot parse amount: '" + amountStr + 
                "'. Please ensure it's a valid number after removing currency symbols and commas.");
                }
            }
            
    /**
     * Builds the ImportException with a formatted error report.
     */
    private static ImportException buildImportException(int successCount, int errorCount, List<String> warnings, Map<Integer, String> lineErrors) {
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("Import completed with errors:\n");
        errorReport.append("- Successfully imported: ").append(successCount).append(" records\n");
        errorReport.append("- Failed to import: ").append(errorCount).append(" records\n\n");
        
        if (!warnings.isEmpty()) {
            errorReport.append("WARNINGS:\n");
            for (String warning : warnings) {
                errorReport.append("- ").append(warning).append("\n");
            }
            errorReport.append("\n");
        }
        
        errorReport.append("ERRORS BY LINE:\n");
        for (Map.Entry<Integer, String> entry : lineErrors.entrySet()) {
            errorReport.append("- Line ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        errorReport.append("\nSuggested solutions:\n");
        errorReport.append("1. Verify CSV delimiter (comma, semicolon, tab) and quoting.\n");
        errorReport.append("2. Check date format - supported formats include YYYY-MM-DD, YYYY/MM/DD, MM/DD/YYYY.\n");
        errorReport.append("3. Ensure amount values contain only numbers, decimal points, and optional currency symbols/commas.\n");
        errorReport.append("4. Check that required columns (Date, Description, Category, Amount) exist and are detected correctly.\n");
        
        return new ImportException(errorReport.toString());
    }

    /**
     * Detects CSV format including delimiter and column positions
     * 
     * @param filePath Path to the CSV file
     * @return Detected CSV format or null if detection fails
     */
    private static CsvFormat detectCsvFormat(String filePath) throws ImportException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                throw new ImportException("CSV file is empty or first line is blank");
            }

            String delimiter = detectDelimiter(firstLine);
            if (delimiter == null) {
                throw new ImportException("Could not detect CSV delimiter (tested comma, semicolon, tab, pipe)");
            }

            // Use the robust parser to get headers correctly
            List<String> headersList = parseCsvLine(firstLine, delimiter);
            String[] headers = headersList.toArray(new String[0]);

            // Read second line to check if headers or data
            String secondLine = reader.readLine();
            boolean hasHeaders = false;
            if (secondLine != null && !secondLine.trim().isEmpty()) {
                 // If first row contains typical header words, assume it's headers
                hasHeaders = isHeaderRow(headers);
            }

            // Find column indices based on header names or assume default order
            int dateColumn = -1, descriptionColumn = -1, categoryColumn = -1, amountColumn = -1;

            if (hasHeaders) {
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i].toLowerCase().trim().replace("\"", ""); // Clean header
                    
                    // Assign index only if not already found (prefer first match)
                    if (dateColumn == -1 && (header.contains("date") || header.contains("日期") || header.contains("时间") || header.contains("time"))) {
                        dateColumn = i;
                    } else if (descriptionColumn == -1 && (header.contains("desc") || header.contains("summary") || header.contains("narrative") || 
                              header.contains("details") || header.contains("摘要") || header.contains("描述") || 
                              header.contains("说明") || header.contains("备注"))) {
                        descriptionColumn = i;
                    } else if (categoryColumn == -1 && (header.contains("categ") || header.contains("type") || header.contains("用途") ||
                              header.contains("类别") || header.contains("类型"))) {
                        categoryColumn = i;
                    } else if (amountColumn == -1 && (header.contains("amount") || header.contains("sum") || header.contains("value") ||
                              header.contains("金额") || header.contains("价格") || header.contains("费用") ||
                              header.contains("价值"))) {
                        amountColumn = i;
                    }
                }
            } else {
                 // No headers detected, assume default order if enough columns
                 if (headers.length >= 4) {
                     dateColumn = 0;
                     descriptionColumn = 1;
                     categoryColumn = 2;
                     amountColumn = 3;
                 } else {
                     throw new ImportException("Cannot determine column order: No headers found and less than 4 columns detected in the first line.");
                 }
            }
            
            // Validate that all required columns were found
            if (dateColumn == -1 || descriptionColumn == -1 || categoryColumn == -1 || amountColumn == -1) {
                 StringBuilder missingCols = new StringBuilder();
                 if (dateColumn == -1) missingCols.append("Date, ");
                 if (descriptionColumn == -1) missingCols.append("Description, ");
                 if (categoryColumn == -1) missingCols.append("Category, ");
                 if (amountColumn == -1) missingCols.append("Amount, ");
                 // Remove trailing comma and space
                 String missingStr = missingCols.substring(0, missingCols.length() - 2);
                 throw new ImportException("Could not find required column(s): " + missingStr + ". Please check headers or file format.");
            }

            System.out.println("Detected CSV Format - Delimiter: '" + delimiter + "', Has Headers: " + hasHeaders + 
                               ", Date Col: " + dateColumn + ", Desc Col: " + descriptionColumn + 
                               ", Cat Col: " + categoryColumn + ", Amt Col: " + amountColumn);
                               
            return new CsvFormat(delimiter, hasHeaders, dateColumn, descriptionColumn, categoryColumn, amountColumn);
        } catch (IOException e) {
            throw new ImportException("Error reading CSV file during format detection: " + e.getMessage(), e);
        }
    }
    
    /**
     * Detect the delimiter used in a CSV line
     * 
     * @param line Sample line from CSV
     * @return Most likely delimiter or null if none detected
     */
    private static String detectDelimiter(String line) {
        String bestDelimiter = null;
        int maxCount = 0;

        for (String delimiter : POSSIBLE_DELIMITERS) {
             // Use the robust parser to count fields based on this delimiter
            int count = parseCsvLine(line, delimiter).size();
            
            // Basic check: If delimiter itself is in quotes, it's less likely
            // Example: field1,"field,2",field3 - comma is less likely than if no quotes contained it
            boolean delimiterInQuotes = false;
            boolean inQuotes = false;
            for(char c : line.toCharArray()) {
                if (c == '"') inQuotes = !inQuotes;
                if (inQuotes && c == delimiter.charAt(0)) {
                    delimiterInQuotes = true;
                    break;
                }
            }

            // Prefer delimiters that yield more fields and are not commonly found within quotes
            if (count > maxCount && count > 1 && !delimiterInQuotes) {
                maxCount = count;
                bestDelimiter = delimiter;
            } else if (count > maxCount && count > 1 && bestDelimiter == null) {
                 // Fallback if all potential delimiters are in quotes
                 maxCount = count;
                 bestDelimiter = delimiter;
            }
        }
        
        return bestDelimiter; 
    }
    
    /**
     * Check if a row is likely to be a header row
     * 
     * @param row The row to check
     * @return True if it's likely a header row
     */
    private static boolean isHeaderRow(String[] row) {
        String headerPattern = "(?i).*(date|time|desc|category|amount|balance|type|日期|时间|描述|金额|类型|类别).*";
        
        // Check if multiple columns match header patterns
        int headerMatches = 0;
        for (String cell : row) {
            if (cell.trim().matches(headerPattern)) {
                headerMatches++;
            }
        }
        
        // If at least 2 columns look like headers, consider it a header row
        return headerMatches >= 2;
    }
    
    /**
     * Class to represent the format of a CSV file
     */
    private static class CsvFormat {
        private final String delimiter;
        private final boolean hasHeaders;
        private final int dateColumn;
        private final int descriptionColumn;
        private final int categoryColumn;
        private final int amountColumn;
        
        public CsvFormat(String delimiter, boolean hasHeaders, int dateColumn, int descriptionColumn, 
                         int categoryColumn, int amountColumn) {
            this.delimiter = delimiter;
            this.hasHeaders = hasHeaders;
            this.dateColumn = dateColumn;
            this.descriptionColumn = descriptionColumn;
            this.categoryColumn = categoryColumn;
            this.amountColumn = amountColumn;
        }
    }
    
    /**
     * Custom exception for CSV import errors with detailed messages
     */
    public static class ImportException extends Exception {
        public ImportException(String message) {
            super(message);
        }
        
        public ImportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static void exportToCSV(String filePath) {
        List<Transaction> transactions = loadTransactions();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("UserId,Date,Description,Category,Amount");
            
            // Write transactions
            for (Transaction transaction : transactions) {
                writer.println(transaction.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads all transactions from all users
     * For admin purposes only
     * @return List of all transactions
     */
    public static List<Transaction> loadAllTransactions() {
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
} 