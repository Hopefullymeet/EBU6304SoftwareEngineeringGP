package view;

import model.BudgetInsights;
import model.Transaction;
import model.TransactionManager;
import model.DeepSeekAPI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * BudgetInsightsPanel - Panel for displaying AI-powered budget insights.
 * This component can be integrated into the BudgetView to show personalized financial insights.
 */
public class BudgetInsightsPanel extends JPanel {
    
    // UI Components
    private JPanel insightsPanel;
    private JPanel controlPanel;
    private JButton refreshButton;
    private JButton generateButton;
    private JTextPane insightsTextPane;
    private StringBuilder insightsContent = new StringBuilder();
    
    // Budget data
    private double totalBudget = 5000.00;
    private double spentAmount = 0.0;
    private Map<String, Double> categoryBreakdown;
    
    // Budget insights generator
    private BudgetInsights budgetInsights;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_BLUE = new Color(230, 246, 255); // Light blue for panel background
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Color ALERT_RED = new Color(231, 76, 60);
    private final Color GREEN = new Color(46, 204, 113);
    private final Color USER_MESSAGE_COLOR = new Color(230, 246, 255);
    private final Color AI_MESSAGE_COLOR = new Color(240, 240, 240);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Constructor for BudgetInsightsPanel
     */
    public BudgetInsightsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        
        // Initialize data
        initializeData();
        
        // Initialize BudgetInsights with an empty string to use default key in DeepSeekAPI
        budgetInsights = new BudgetInsights("");
        
        createHeaderPanel();
        createInsightsPanel();
        createControlPanel();
    }
    
    /**
     * Initialize data from transaction manager
     */
    private void initializeData() {
        categoryBreakdown = new HashMap<>();
        
        // Load real transactions
        List<Transaction> transactions = TransactionManager.loadTransactions();
        
        // Calculate total spent amount and category breakdown
        spentAmount = 0.0;
        
        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();
            spentAmount += amount;
            
            // Update category breakdown
            String category = transaction.getCategory();
            categoryBreakdown.put(
                category, 
                categoryBreakdown.getOrDefault(category, 0.0) + amount
            );
        }
        
        // If no transactions found, use sample data
        if (transactions.isEmpty()) {
            initializeSampleData();
        }
    }
    
    /**
     * Initialize sample data for demonstration when no transactions exist
     */
    private void initializeSampleData() {
        categoryBreakdown = new HashMap<>();
        categoryBreakdown.put("Housing", 1500.0);
        categoryBreakdown.put("Food & Dining", 650.25);
        categoryBreakdown.put("Transportation", 420.50);
        categoryBreakdown.put("Utilities", 340.0);
        categoryBreakdown.put("Entertainment", 200.0);
        categoryBreakdown.put("Other", 130.0);
        
        spentAmount = 3240.75;
    }
    
    /**
     * Creates the header panel with title
     */
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("AI Budget Insights");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_BLUE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the insights panel that displays AI-generated insights
     */
    private void createInsightsPanel() {
        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(Color.WHITE);
        
        // Budget summary section
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.setBackground(LIGHT_BLUE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        // Show current budget data
        JLabel budgetLabel = new JLabel(String.format(
            "Monthly Budget: ¥%.2f   |   Spent: ¥%.2f   |   Remaining: ¥%.2f", 
            totalBudget, spentAmount, totalBudget - spentAmount
        ));
        budgetLabel.setFont(SUBHEADER_FONT);
        budgetLabel.setForeground(PRIMARY_BLUE);
        
        summaryPanel.add(budgetLabel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // General insights section
        JPanel generalInsightsPanel = new JPanel(new BorderLayout());
        generalInsightsPanel.setBackground(LIGHT_BLUE);
        generalInsightsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Header for general insights
        JPanel generalHeaderPanel = new JPanel(new BorderLayout());
        generalHeaderPanel.setBackground(LIGHT_BLUE);
        JLabel generalLabel = new JLabel("Budget Insights");
        generalLabel.setFont(SUBHEADER_FONT);
        generalLabel.setForeground(DARK_GRAY);
        generalHeaderPanel.add(generalLabel, BorderLayout.WEST);
        
        // Generate button in the general insights header
        generateButton = new JButton("Generate");
        generateButton.setBackground(PRIMARY_BLUE);
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setFocusPainted(false);
        generateButton.setBorderPainted(false);
        generateButton.addActionListener(e -> generateInsights());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(LIGHT_BLUE);
        buttonPanel.add(generateButton);
        generalHeaderPanel.add(buttonPanel, BorderLayout.EAST);
        
        generalInsightsPanel.add(generalHeaderPanel, BorderLayout.NORTH);
        
        // Text pane for general insights with proper styling
        insightsTextPane = createStyledTextPane();
        insightsTextPane.setText("Click 'Generate' to create personalized budget insights based on your spending patterns.");
        JScrollPane insightsScrollPane = new JScrollPane(insightsTextPane);
        insightsScrollPane.setBorder(null);
        insightsScrollPane.setPreferredSize(new Dimension(600, 350));
        generalInsightsPanel.add(insightsScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(generalInsightsPanel, BorderLayout.CENTER);
        
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates a styled text pane for rich text display
     * @return A configured JTextPane
     */
    private JTextPane createStyledTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        
        // Define styles
        Style defaultStyle = textPane.getStyledDocument().addStyle("default", null);
        StyleConstants.setFontFamily(defaultStyle, "Arial");
        StyleConstants.setFontSize(defaultStyle, 14);
        
        Style bulletStyle = textPane.getStyledDocument().addStyle("bullet", defaultStyle);
        StyleConstants.setBold(bulletStyle, true);
        StyleConstants.setForeground(bulletStyle, PRIMARY_BLUE);
        
        Style alertStyle = textPane.getStyledDocument().addStyle("alert", defaultStyle);
        StyleConstants.setForeground(alertStyle, ALERT_RED);
        
        return textPane;
    }
    
    /**
     * Creates the control panel with refresh button
     */
    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(CONTENT_FONT);
        refreshButton.addActionListener(e -> refreshData());
        
        JCheckBox autoRefreshCheckBox = new JCheckBox("Auto-refresh when data changes");
        autoRefreshCheckBox.setFont(CONTENT_FONT);
        autoRefreshCheckBox.setBackground(Color.WHITE);
        autoRefreshCheckBox.setSelected(true);
        
        controlPanel.add(refreshButton);
        controlPanel.add(autoRefreshCheckBox);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the budget data without generating new insights
     */
    private void refreshData() {
        // Reload data
        initializeData();
        
        // Update budget summary
        updateBudgetSummary();
    }
    
    /**
     * Updates the budget summary display
     */
    private void updateBudgetSummary() {
        // Find the budget label in the UI and update it
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JPanel) {
                    JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
                    for (Component c : contentPanel.getComponents()) {
                        if (c instanceof JPanel && c == contentPanel.getComponent(0)) {
                            JPanel summaryPanel = (JPanel) c;
                            for (Component sc : summaryPanel.getComponents()) {
                                if (sc instanceof JLabel) {
                                    JLabel budgetLabel = (JLabel) sc;
                                    budgetLabel.setText(String.format(
                                        "Monthly Budget: ¥%.2f   |   Spent: ¥%.2f   |   Remaining: ¥%.2f", 
                                        totalBudget, spentAmount, totalBudget - spentAmount
                                    ));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Generates AI insights when the generate button is clicked
     */
    private void generateInsights() {
        // Disable generate button during processing
        generateButton.setEnabled(false);
        generateButton.setText("Generating...");
        
        // Reset insights content buffer
        insightsContent.setLength(0);
        
        // Clear text pane and show loading message
        clearTextPane(insightsTextPane);
        appendToTextPane(insightsTextPane, "Generating personalized budget insights...\n", "default");
        
        // Generate new general insights
        budgetInsights.generateGeneralInsights(totalBudget, spentAmount, categoryBreakdown, insights -> {
            // Update UI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    // Check if this is a streaming chunk or final result
                    if (insights.size() == 1 && !insights.get(0).startsWith("•")) {
                        // This is a streaming chunk, append to the text pane
                        String chunk = insights.get(0);
                        
                        // Add to our content buffer
                        insightsContent.append(chunk);
                        
                        // Remove previous content and show updated full content
                        clearTextPane(insightsTextPane);
                        appendStyledContent(insightsTextPane, insightsContent.toString());
                    } else {
                        // This is the final result with parsed insights
                        
                        // If we have accumulated content and the final result only has one item
                        // that starts with "•", it means the parser might have reduced all content to a single bullet
                        if (insightsContent.length() > 0 && 
                            insights.size() == 1 && 
                            insights.get(0).startsWith("•")) {
                            
                            System.out.println("Detected possible parsing issue. Using accumulated content instead.");
                            
                            // Process our accumulated content - format it nicely
                            String fullContent = insightsContent.toString();
                            clearTextPane(insightsTextPane);
                            
                            // Split by double line breaks or periods followed by space to create bullet points
                            String[] parts = fullContent.split("(?<=\\.)\\s+|\\n\\n+");
                            
                            for (String part : parts) {
                                if (part.trim().isEmpty()) continue;
                                
                                // Add each part as a separate insight
                                String formattedInsight = "• " + part.trim();
                                appendInsightToTextPane(insightsTextPane, formattedInsight);
                            }
                        } else {
                            // Use the parsed insights as they appear valid
                            clearTextPane(insightsTextPane);
                            
                            if (insights.isEmpty()) {
                                // If no insights, use the accumulated content
                                if (insightsContent.length() > 0) {
                                    appendStyledContent(insightsTextPane, insightsContent.toString());
                                } else {
                                    appendToTextPane(insightsTextPane, 
                                        "• No insights generated. Please try again.", "default");
                                }
                            } else {
                                // Add each insight to the text pane
                                for (String insight : insights) {
                                    appendInsightToTextPane(insightsTextPane, insight);
                                }
                            }
                        }
                        
                        // Re-enable generate button
                        generateButton.setEnabled(true);
                        generateButton.setText("Generate");
                    }
                } catch (Exception e) {
                    // Handle any unexpected errors in the UI update
                    System.err.println("Error updating insights display: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Show error in the UI
                    clearTextPane(insightsTextPane);
                    appendToTextPane(insightsTextPane, 
                        "• Error displaying insights: " + e.getMessage() + "\n\nPlease try again.", "alert");
                    
                    // Re-enable generate button
                    generateButton.setEnabled(true);
                    generateButton.setText("Generate");
                }
            });
        });
    }
    
    /**
     * Appends insights to the text pane with proper styling
     * @param textPane The text pane to append to
     * @param insight The insight text
     */
    private void appendInsightToTextPane(JTextPane textPane, String insight) {
        StyledDocument doc = textPane.getStyledDocument();
        
        try {
            if (insight.startsWith("•")) {
                // Handle bullet point differently, with proper styling
                String bulletPart = "• ";
                String contentPart = insight.substring(2).trim();
                
                // Skip empty insights
                if (contentPart.isEmpty()) return;
                
                doc.insertString(doc.getLength(), bulletPart, doc.getStyle("bullet"));
                
                // Check for alerts
                if (contentPart.toLowerCase().contains("alert:")) {
                    doc.insertString(doc.getLength(), contentPart, doc.getStyle("alert"));
                } else {
                    doc.insertString(doc.getLength(), contentPart, doc.getStyle("default"));
                }
                
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("default"));
            } else {
                // Regular text - add bullet anyway for consistency
                String contentPart = insight.trim();
                
                // Skip empty insights
                if (contentPart.isEmpty()) return;
                
                doc.insertString(doc.getLength(), "• ", doc.getStyle("bullet"));
                doc.insertString(doc.getLength(), contentPart, doc.getStyle("default"));
                doc.insertString(doc.getLength(), "\n\n", doc.getStyle("default"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        // Scroll to the bottom
        textPane.setCaretPosition(doc.getLength());
    }
    
    /**
     * Appends text to a text pane with the specified style
     * @param textPane The text pane to append to
     * @param text The text to append
     * @param styleName The name of the style to use
     */
    private void appendToTextPane(JTextPane textPane, String text, String styleName) {
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), text, doc.getStyle(styleName));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        // Scroll to the bottom
        textPane.setCaretPosition(doc.getLength());
    }
    
    /**
     * Appends styled content to the text pane, applying paragraph formatting
     * @param textPane The text pane to append to
     * @param content The content to append
     */
    private void appendStyledContent(JTextPane textPane, String content) {
        StyledDocument doc = textPane.getStyledDocument();
        
        try {
            // Apply AI message style
            Style contentStyle = doc.getStyle("default");
            if (contentStyle == null) {
                contentStyle = doc.addStyle("default", null);
                StyleConstants.setFontFamily(contentStyle, "Arial");
                StyleConstants.setFontSize(contentStyle, 14);
            }
            
            // Create paragraph attributes for proper formatting
            SimpleAttributeSet paragraphAttrs = new SimpleAttributeSet();
            StyleConstants.setLeftIndent(paragraphAttrs, 5);
            StyleConstants.setRightIndent(paragraphAttrs, 5);
            StyleConstants.setSpaceAbove(paragraphAttrs, 3);
            StyleConstants.setSpaceBelow(paragraphAttrs, 3);
            
            // Apply word-wrap friendly line breaks - convert double spaces to line breaks 
            // for better formatting during streaming
            String formattedContent = content.replaceAll("\\. {2,}", ".\n\n");
            
            // Insert content
            doc.insertString(doc.getLength(), formattedContent, contentStyle);
            
            // Apply paragraph attributes
            doc.setParagraphAttributes(0, doc.getLength(), paragraphAttrs, false);
            
            // Scroll to bottom
            textPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Clears a text pane
     * @param textPane The text pane to clear
     */
    private void clearTextPane(JTextPane textPane) {
        textPane.setText("");
    }
    
    /**
     * Updates the budget data and refreshes displayed data
     * @param totalBudget The total budget amount
     * @param spentAmount The amount already spent
     * @param categoryBreakdown Map of categories to amounts
     */
    public void updateBudgetData(double totalBudget, double spentAmount, Map<String, Double> categoryBreakdown) {
        this.totalBudget = totalBudget;
        this.spentAmount = spentAmount;
        this.categoryBreakdown = categoryBreakdown;
        
        // Update the budget summary
        updateBudgetSummary();
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
        JFrame frame = new JFrame("Budget Insights Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new BudgetInsightsPanel());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
} 