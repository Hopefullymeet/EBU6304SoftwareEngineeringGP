package view;

import model.TransactionCategorizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TransactionCategoryPanel - Panel for AI-assisted transaction categorization.
 * This component can be integrated into the BillingView for automatic transaction categorization.
 */
public class TransactionCategoryPanel extends JPanel {
    
    // UI Components
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JCheckBox overrideCheckBox;
    private JButton categorizeButton;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JButton categorizeAllButton;
    
    // API connection components
    private JTextField apiKeyField;
    private JButton connectButton;
    private boolean isConnected = false;
    
    // Transaction categorizer
    private TransactionCategorizer categorizer;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Constructor for TransactionCategoryPanel
     */
    public TransactionCategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        createHeaderPanel();
        createInputPanel();
        createTablePanel();
        createApiKeyPanel();
        
        // 应用红黄主题（如果被嵌入到JFrame中）
        java.awt.Window win = SwingUtilities.getWindowAncestor(this);
        if (win instanceof JFrame) {
            AccountView.applyThemeToAllComponents((JFrame)win);
        }
    }
    
    /**
     * Creates the header panel with title
     */
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("AI Transaction Categorization");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        
        statusLabel = new JLabel("Status: Not connected to AI service");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(Color.RED);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the input panel for transaction details
     */
    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(LIGHT_GRAY);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Description input
        JLabel descriptionLabel = new JLabel("Transaction Description:");
        descriptionLabel.setFont(CONTENT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(descriptionLabel, gbc);
        
        descriptionField = new JTextField();
        descriptionField.setFont(CONTENT_FONT);
        descriptionField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        inputPanel.add(descriptionField, gbc);
        
        // Amount input
        JLabel amountLabel = new JLabel("Amount (¥):");
        amountLabel.setFont(CONTENT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(amountLabel, gbc);
        
        amountField = new JTextField();
        amountField.setFont(CONTENT_FONT);
        amountField.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        inputPanel.add(amountField, gbc);
        
        // Category selection
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(CONTENT_FONT);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        inputPanel.add(categoryLabel, gbc);
        
        categoryComboBox = new JComboBox<>(TransactionCategorizer.CATEGORIES);
        categoryComboBox.setFont(CONTENT_FONT);
        categoryComboBox.setEnabled(false);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        inputPanel.add(categoryComboBox, gbc);
        
        // Override checkbox
        overrideCheckBox = new JCheckBox("Override AI suggestion");
        overrideCheckBox.setFont(CONTENT_FONT);
        overrideCheckBox.setBackground(LIGHT_GRAY);
        overrideCheckBox.setEnabled(false);
        overrideCheckBox.addActionListener(e -> categoryComboBox.setEnabled(overrideCheckBox.isSelected()));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(overrideCheckBox, gbc);
        
        // Categorize button
        categorizeButton = new JButton("Categorize");
        categorizeButton.setFont(CONTENT_FONT);
        categorizeButton.setBackground(PRIMARY_BLUE);
        categorizeButton.setForeground(Color.WHITE);
        categorizeButton.setEnabled(false);
        categorizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                categorizeTransaction();
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(categorizeButton, gbc);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrapperPanel.add(inputPanel, BorderLayout.CENTER);
        
        add(wrapperPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the table panel for transaction display
     */
    private void createTablePanel() {
        // Create table model with columns
        String[] columns = {"Description", "Amount", "AI Suggested Category", "Final Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing the Final Category column
                return column == 3;
            }
        };
        
        transactionsTable = new JTable(tableModel);
        transactionsTable.setFont(CONTENT_FONT);
        transactionsTable.setRowHeight(25);
        transactionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Configure the Final Category column as a combo box
        JComboBox<String> categoriesCombo = new JComboBox<>(TransactionCategorizer.CATEGORIES);
        DefaultCellEditor cellEditor = new DefaultCellEditor(categoriesCombo);
        transactionsTable.getColumnModel().getColumn(3).setCellEditor(cellEditor);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        // Bottom panel with categorization buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        categorizeAllButton = new JButton("Categorize All");
        categorizeAllButton.setFont(CONTENT_FONT);
        categorizeAllButton.setBackground(PRIMARY_BLUE);
        categorizeAllButton.setForeground(Color.WHITE);
        categorizeAllButton.setEnabled(false);
        categorizeAllButton.addActionListener(e -> categorizeAllTransactions());
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(CONTENT_FONT);
        clearButton.addActionListener(e -> clearTable());
        
        JButton importButton = new JButton("Import Transactions");
        importButton.setFont(CONTENT_FONT);
        
        bottomPanel.add(importButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(categorizeAllButton);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        tableContainer.add(bottomPanel, BorderLayout.SOUTH);
        
        add(tableContainer, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the API key panel
     */
    private void createApiKeyPanel() {
        JPanel apiPanel = new JPanel();
        apiPanel.setLayout(new BoxLayout(apiPanel, BoxLayout.Y_AXIS));
        apiPanel.setBackground(Color.WHITE);
        apiPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("API Connection"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JLabel apiKeyLabel = new JLabel("DeepSeek API Key:");
        apiKeyLabel.setFont(CONTENT_FONT);
        apiKeyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        apiKeyField = new JTextField();
        apiKeyField.setFont(CONTENT_FONT);
        apiKeyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        apiKeyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        connectButton = new JButton("Connect");
        connectButton.setFont(CONTENT_FONT);
        connectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        connectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // Add action listener to connect button
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    String apiKey = apiKeyField.getText().trim();
                    if (apiKey.isEmpty()) {
                        JOptionPane.showMessageDialog(TransactionCategoryPanel.this, 
                            "Please enter your DeepSeek API Key", 
                            "API Key Required", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    try {
                        // Initialize the categorizer
                        categorizer = new TransactionCategorizer(apiKey);
                        isConnected = true;
                        
                        // Update UI
                        statusLabel.setText("Status: Connected to AI service");
                        statusLabel.setForeground(new Color(46, 204, 113)); // Green
                        connectButton.setText("Disconnect");
                        apiKeyField.setEnabled(false);
                        
                        // Enable transaction input
                        descriptionField.setEnabled(true);
                        amountField.setEnabled(true);
                        overrideCheckBox.setEnabled(true);
                        categorizeButton.setEnabled(true);
                        
                        // Enable categorize all button if there are transactions
                        if (tableModel.getRowCount() > 0) {
                            categorizeAllButton.setEnabled(true);
                        }
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(TransactionCategoryPanel.this, 
                            "Failed to connect: " + ex.getMessage(), 
                            "Connection Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Disconnect
                    isConnected = false;
                    categorizer = null;
                    
                    // Update UI
                    statusLabel.setText("Status: Not connected to AI service");
                    statusLabel.setForeground(Color.RED);
                    connectButton.setText("Connect");
                    apiKeyField.setEnabled(true);
                    
                    // Disable transaction input
                    descriptionField.setEnabled(false);
                    amountField.setEnabled(false);
                    categoryComboBox.setEnabled(false);
                    overrideCheckBox.setEnabled(false);
                    overrideCheckBox.setSelected(false);
                    categorizeButton.setEnabled(false);
                    
                    // Disable categorize all button
                    categorizeAllButton.setEnabled(false);
                }
            }
        });
        
        apiPanel.add(apiKeyLabel);
        apiPanel.add(Box.createVerticalStrut(5));
        apiPanel.add(apiKeyField);
        apiPanel.add(Box.createVerticalStrut(10));
        apiPanel.add(connectButton);
        
        add(apiPanel, BorderLayout.EAST);
    }
    
    /**
     * Categorizes a single transaction and adds it to the table
     */
    private void categorizeTransaction() {
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        
        if (description.isEmpty() || amountText.isEmpty()) {
            statusLabel.setText("Status: Please enter both description and amount");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            statusLabel.setText("Status: Invalid amount format");
            return;
        }
        
        // Check if the categorize all button is enabled and disable it during processing
        categorizeAllButton.setEnabled(false);
        
        // Disable categorize button during processing
        categorizeButton.setEnabled(false);
        statusLabel.setText("Status: Processing...");
        
        // Use SwingWorker to avoid freezing the UI
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                if (!isConnected) {
                    JOptionPane.showMessageDialog(TransactionCategoryPanel.this, 
                        "Please connect to the API first", 
                        "Not Connected", 
                        JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                
                try {
                    // Use AI categorization
                    categorizer.categorizeTransaction(description, amount, category -> {
                        // Add to table on EDT
                        SwingUtilities.invokeLater(() -> {
                            Object[] row = {description, String.format("¥%.2f", amount), category, category};
                            tableModel.addRow(row);
                            
                            // Re-enable inputs
                            setCategoryInputsEnabled(true);
                            statusLabel.setText("Status: Connected to AI service");
                            clearInputFields();
                        });
                    });
                    
                    return "Transaction categorized successfully";
                } catch (Exception ex) {
                    return "Failed to categorize transaction: " + ex.getMessage();
                }
            }
            
            @Override
            protected void done() {
                String result = null;
                try {
                    result = get();
                } catch (Exception ex) {
                    result = "An error occurred: " + ex.getMessage();
                }
                
                if (result != null) {
                    JOptionPane.showMessageDialog(TransactionCategoryPanel.this, result, "Transaction Categorization", JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Enable categorize all button
                if (tableModel.getRowCount() > 0) {
                    categorizeAllButton.setEnabled(true);
                }
                
                // Re-enable categorize button
                categorizeButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    /**
     * Categorizes all transactions in the table
     */
    private void categorizeAllTransactions() {
        if (!isConnected || tableModel.getRowCount() == 0) {
            return;
        }
        
        // Disable categorize button during processing
        categorizeAllButton.setEnabled(false);
        
        statusLabel.setText("Status: Processing all transactions...");
        
        // Collect all transactions
        List<Map<String, Object>> transactions = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("description", tableModel.getValueAt(i, 0));
            
            // Parse amount from string (remove ¥ and parse)
            String amountStr = (String) tableModel.getValueAt(i, 1);
            double amount = Double.parseDouble(amountStr.replace("¥", ""));
            transaction.put("amount", amount);
            
            transactions.add(transaction);
        }
        
        // Send for categorization
        categorizer.categorizeTransactions(transactions, categories -> {
            // Update table on EDT
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < categories.size() && i < tableModel.getRowCount(); i++) {
                    String category = categories.get(i);
                    tableModel.setValueAt(category, i, 2); // Update AI suggested category
                    tableModel.setValueAt(category, i, 3); // Update final category
                }
                
                // Re-enable categorize button
                categorizeAllButton.setEnabled(true);
                
                statusLabel.setText("Status: Connected to AI service");
            });
        });
    }
    
    /**
     * Clears the transaction table
     */
    private void clearTable() {
        tableModel.setRowCount(0);
        
        // Disable categorize all button
        categorizeAllButton.setEnabled(false);
    }
    
    /**
     * Clears the input fields
     */
    private void clearInputFields() {
        descriptionField.setText("");
        amountField.setText("");
        overrideCheckBox.setSelected(false);
        categoryComboBox.setEnabled(false);
        categoryComboBox.setSelectedIndex(0);
    }
    
    /**
     * Enables or disables the category input fields
     * @param enabled Whether the fields should be enabled
     */
    private void setCategoryInputsEnabled(boolean enabled) {
        descriptionField.setEnabled(enabled);
        amountField.setEnabled(enabled);
        overrideCheckBox.setEnabled(enabled);
        categorizeButton.setEnabled(enabled);
        // Only enable category combo box if override is selected
        categoryComboBox.setEnabled(enabled && overrideCheckBox.isSelected());
    }
    
    /**
     * Get the list of categorized transactions
     * @return A list of maps with transaction details
     */
    public List<Map<String, Object>> getCategorizedTransactions() {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("description", tableModel.getValueAt(i, 0));
            transaction.put("amount", tableModel.getValueAt(i, 1));
            transaction.put("suggestedCategory", tableModel.getValueAt(i, 2));
            transaction.put("finalCategory", tableModel.getValueAt(i, 3));
            transactions.add(transaction);
        }
        
        return transactions;
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create frame for testing
        JFrame frame = new JFrame("Transaction Categorization Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.add(new TransactionCategoryPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 