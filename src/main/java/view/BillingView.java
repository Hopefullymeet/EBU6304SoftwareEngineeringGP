package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Transaction;
import model.TransactionManager;
import model.DeepSeekAPI;
import model.CurrencyManager;
import model.User;
import model.UserManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.SessionManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * BillingView - The billing and subscriptions screen with transaction management.
 * This class displays billing information and allows transaction data entry.
 */
public class BillingView extends JFrame {
    
    // UI Components
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Sidebar items
    private JPanel accountPanel;
    private JPanel billingPanel;
    private JPanel budgetPanel;
    private JPanel helpCenterPanel;
    private JPanel financialAdvisorPanel;
    private JPanel manualEntryPanel;
    private JPanel importPanel;
    
    // Billing sub-items container
    private JPanel billingSubItems;
    private boolean billingExpanded = true;
    
    // Transactions panel
    private JPanel transactionsPanel;
    
    // Managers for currency and theme
    private CurrencyManager currencyManager;
    
    // Static shared styling
    private static final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color DARK_GRAY = new Color(100, 100, 100);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    private User currentUser;
    private JTable billingTable;
    
    /**
     * Constructor for the BillingView
     */
    public BillingView() {
        setTitle("Billing & Subscriptions");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize managers
        currencyManager = CurrencyManager.getInstance();
        
        // Apply current user's theme
        currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            // themeManager.applyUserThemePreference(currentUser);
        }
        
        createHeader();
        createSidebar();
        
        // Create content with card layout for multiple views
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        createBillingContent();
        createManualEntryPanel();
        createImportPanel();
        
        // Show billing panel by default
        cardLayout.show(contentPanel, "billing");
        setActiveSidebarItem(null);
        
        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Start session monitoring
        SessionManager.getInstance().startSession(this);
        
        refreshTransactionsDisplay();
    }
    
    /**
     * Creates the header panel with title and logout button
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Billing & Data Entry");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create logout button in the top-right corner
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60)); // Red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        
        // Create a panel to hold the logout button with some margin
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(PRIMARY_BLUE);
        logoutPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        logoutPanel.add(logoutButton);
        
        // Add action listener to logout button
        logoutButton.addActionListener(e -> {
            // Stop session monitoring
            SessionManager.getInstance().stopSession();
            
            // Log out the user
            UserManager.getInstance().logout();
            
            dispose(); // Close current window
            new LoginView(); // Return to login screen
        });
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the sidebar navigation panel
     */
    private void createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // Create sidebar items
        accountPanel = createSidebarItem("Account", false, false);
        billingPanel = createSidebarItem("Billing & Subscriptions", false, true);
        budgetPanel = createSidebarItem("Budget", false, false);
        helpCenterPanel = createSidebarItem("Help Center", false, false);
        financialAdvisorPanel = createSidebarItem("Financial Advisor", false, false);
        manualEntryPanel = createSidebarItem("Manual Entry", true, false);
        importPanel = createSidebarItem("Import Transactions", true, false);
        
        // Create Billing sub-items container
        billingSubItems = new JPanel();
        billingSubItems.setLayout(new BoxLayout(billingSubItems, BoxLayout.Y_AXIS));
        billingSubItems.setBackground(Color.WHITE);
        billingSubItems.setVisible(billingExpanded);
        
        // Create Billing sub-items
        JPanel billingOverviewPanel = createSidebarItem("Overview", true, false);
        JPanel manualEntryItemPanel = createSidebarItem("Manual Entry", true, false);
        JPanel importItemPanel = createSidebarItem("Import Transactions", true, false);
        
        // Add click listeners for main navigation
        accountPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AccountView();
            }
        });
        
        billingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                toggleBillingSubItems();
            }
        });
        
        budgetPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new BudgetView();
            }
        });
        
        helpCenterPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new HelpCenterView();
            }
        });
        
        financialAdvisorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new FinancialAdvisorView();
            }
        });
        
        // Add click listeners for Billing sub-panels
        billingOverviewPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "billing");
                setActiveSidebarItem(billingOverviewPanel);
            }
        });
        
        manualEntryItemPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "manualEntry");
                setActiveSidebarItem(manualEntryItemPanel);
            }
        });
        
        importItemPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                cardLayout.show(contentPanel, "import");
                setActiveSidebarItem(importItemPanel);
            }
        });
        
        // Add sub-items to container
        billingSubItems.add(billingOverviewPanel);
        billingSubItems.add(Box.createVerticalStrut(5));
        billingSubItems.add(manualEntryItemPanel);
        billingSubItems.add(Box.createVerticalStrut(5));
        billingSubItems.add(importItemPanel);
        
        // Add sidebar items to panel
        sidebarPanel.add(accountPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billingPanel);
        sidebarPanel.add(billingSubItems);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(helpCenterPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(financialAdvisorPanel);
        
        // Add a glue component to push everything to the top
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Add sidebar to main frame
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * Toggle the visibility of Billing sub-items
     */
    private void toggleBillingSubItems() {
        billingExpanded = !billingExpanded;
        billingSubItems.setVisible(billingExpanded);
        
        // Add arrow indicator to Billing item
        JLabel billingLabel = (JLabel) ((JPanel) billingPanel.getComponent(0)).getComponent(0);
        billingLabel.setText("Billing & Subscriptions " + (billingExpanded ? "▼" : "▶"));
        
        // Refresh the UI
        billingPanel.revalidate();
        billingPanel.repaint();
    }
    
    /**
     * Sets the active sidebar item by changing its background color
     * @param activePanel The panel to set as active
     */
    private void setActiveSidebarItem(JPanel activePanel) {
        // Collect all sub-items
        Component[] subItems = billingSubItems.getComponents();
        
        // Reset background of all items
        for (Component comp : subItems) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(Color.WHITE);
                if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JPanel) {
                    panel.getComponent(0).setBackground(Color.WHITE);
                }
            }
        }
        
        // Set active panel if not null
        if (activePanel != null) {
            activePanel.setBackground(LIGHT_GRAY);
            if (activePanel.getComponentCount() > 0 && activePanel.getComponent(0) instanceof JPanel) {
                activePanel.getComponent(0).setBackground(LIGHT_GRAY);
            }
        }
    }
    
    /**
     * Creates a sidebar item
     * @param text The text for the sidebar item
     * @param isSubItem Whether this is a sub-item (indented)
     * @param isActive Whether this item is currently active
     * @return The created panel
     */
    private JPanel createSidebarItem(String text, boolean isSubItem, boolean isActive) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBackground(isActive ? LIGHT_GRAY : Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // Inner panel to hold the label for proper alignment
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(isActive ? LIGHT_GRAY : Color.WHITE);
        
        String displayText = text;
        if (text.equals("Billing & Subscriptions")) {
            displayText = text + " " + (billingExpanded ? "▼" : "▶");
        }
        
        JLabel textLabel = new JLabel(displayText);
        textLabel.setFont(SIDEBAR_FONT);
        textLabel.setForeground(DARK_GRAY);
        
        if (isSubItem) {
            labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 5));
        } else {
            labelPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        
        labelPanel.add(textLabel);
        itemPanel.add(labelPanel, BorderLayout.CENTER);
        
        // Add hover effect if not active
        if (!isActive) {
            itemPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (itemPanel.getBackground() != LIGHT_GRAY) {
                        itemPanel.setBackground(new Color(250, 250, 250));
                        labelPanel.setBackground(new Color(250, 250, 250));
                    }
                }
                
                public void mouseExited(MouseEvent evt) {
                    if (itemPanel.getBackground() != LIGHT_GRAY) {
                        itemPanel.setBackground(Color.WHITE);
                        labelPanel.setBackground(Color.WHITE);
                    }
                }
            });
        }
        
        return itemPanel;
    }
    
    /**
     * Creates the billing content panel
     */
    private void createBillingContent() {
        JPanel billingPanel = new JPanel();
        billingPanel.setBackground(Color.WHITE);
        billingPanel.setLayout(new BorderLayout(10, 10));
        billingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        
        // Subscription section
        JLabel subscriptionLabel = new JLabel("Your Subscription");
        subscriptionLabel.setFont(SUBHEADER_FONT);
        subscriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel planInfoPanel = new JPanel();
        planInfoPanel.setLayout(new BorderLayout());
        planInfoPanel.setBackground(LIGHT_GRAY);
        planInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        planInfoPanel.setMaximumSize(new Dimension(800, 80));
        planInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel planNameLabel = new JLabel("Finance Tracker Pro");
        planNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel planDetailsLabel = new JLabel("<html>Subscription active • Next billing date: 2024/01/01</html>");
        planDetailsLabel.setFont(CONTENT_FONT);
        
        planInfoPanel.add(planNameLabel, BorderLayout.NORTH);
        planInfoPanel.add(planDetailsLabel, BorderLayout.CENTER);
        
        // Recent transactions title
        JLabel transactionsLabel = new JLabel("Recent Transactions");
        transactionsLabel.setFont(SUBHEADER_FONT);
        transactionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transactionsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        // Add some sample transactions using lighter weight components
        transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BoxLayout(transactionsPanel, BoxLayout.Y_AXIS));
        transactionsPanel.setBackground(Color.WHITE);
        transactionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add a few sample transactions with YYYY/MM/DD format
        addTransactionItem(transactionsPanel, "2024-01-15", "Grocery Shopping", "Food", 85.43);
        addTransactionItem(transactionsPanel, "2024-01-12", "Monthly Rent", "Housing", 1200.00);
        addTransactionItem(transactionsPanel, "2024-01-10", "Gas Station", "Transportation", 45.75);
        addTransactionItem(transactionsPanel, "2024-01-05", "Online Streaming", "Entertainment", 14.99);
        
        // Data entry instructions
        JLabel dataEntryLabel = new JLabel("Transaction Data Entry");
        dataEntryLabel.setFont(SUBHEADER_FONT);
        dataEntryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dataEntryLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
        instructionsPanel.setBackground(LIGHT_GRAY);
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        instructionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsPanel.setMaximumSize(new Dimension(800, 150)); // 限制最大尺寸，减少空白区域
        
        JLabel instructionsText = new JLabel("<html>" +
                "<p>You can add transaction data in two ways:</p>" +
                "<p>1. <b>Manual Entry</b>: Add individual transactions manually</p>" +
                "<p>2. <b>Import Transactions</b>: Import CSV files from your bank</p>" +
                "</html>");
        instructionsText.setFont(CONTENT_FONT);
        instructionsText.setAlignmentX(Component.LEFT_ALIGNMENT); // 确保文本左对齐
        
        // 创建一个包装面板，确保按钮左对齐
        JPanel buttonsWrapper = new JPanel();
        buttonsWrapper.setLayout(new BoxLayout(buttonsWrapper, BoxLayout.X_AXIS));
        buttonsWrapper.setBackground(LIGHT_GRAY);
        buttonsWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // 设置FlowLayout的水平和垂直间距为0
        buttonsPanel.setBackground(LIGHT_GRAY);
        
        JButton manualButton = new JButton("Manual Entry");
        manualButton.setFont(CONTENT_FONT);
        manualButton.setFocusPainted(false);
        manualButton.addActionListener(e -> cardLayout.show(contentPanel, "manualEntry"));
        
        JButton importButton = new JButton("Import Transactions");
        importButton.setFont(CONTENT_FONT);
        importButton.setFocusPainted(false);
        importButton.addActionListener(e -> cardLayout.show(contentPanel, "import"));
        
        buttonsPanel.add(manualButton);
        buttonsPanel.add(importButton);
        
        buttonsWrapper.add(buttonsPanel);
        buttonsWrapper.add(Box.createHorizontalGlue()); // 添加水平胶水，确保按钮左对齐
        
        instructionsPanel.add(instructionsText);
        instructionsPanel.add(Box.createVerticalStrut(10));
        instructionsPanel.add(buttonsWrapper);
        
        // Add everything to the main content panel
        mainContent.add(subscriptionLabel);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(planInfoPanel);
        mainContent.add(Box.createVerticalStrut(10));
        mainContent.add(transactionsLabel);
        mainContent.add(transactionsPanel);
        mainContent.add(dataEntryLabel);
        mainContent.add(instructionsPanel);
        
        // Add a scroll pane for the main content
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        billingPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(billingPanel, "billing");
    }
    
    /**
     * Creates the manual transaction entry panel
     */
    private void createManualEntryPanel() {
        JPanel manualEntryPanel = new JPanel();
        manualEntryPanel.setBackground(Color.WHITE);
        manualEntryPanel.setLayout(new BorderLayout(10, 10));
        manualEntryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Manual Transaction Entry");
        headerLabel.setFont(SUBHEADER_FONT);
        
        // Create the form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Date - Updated format for Chinese user preference (YYYY/MM/DD)
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(10);
        dateField.setText("yyyy-MM-dd");
        
        // Description
        JLabel descLabel = new JLabel("Description:");
        JTextField descField = new JTextField(20);
        
        // Amount - Clarify meaning
        JLabel amountLabel = new JLabel("Expense Amount ($):");
        JTextField amountField = new JTextField(10);
        
        // Category
        JLabel categoryLabel = new JLabel("Category:");
        // Add AI categorization option
        String[] categories = {"Select Category", "AI Categorize", "Housing", "Food", "Transportation", "Entertainment", "Shopping", "Utilities", "Chinese New Year", "Education", "Medical", "Travel", "Other"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        
        // AI Suggestion section - Improved
        JPanel aiSuggestionPanel = new JPanel();
        aiSuggestionPanel.setLayout(new BorderLayout());
        aiSuggestionPanel.setBackground(new Color(240, 248, 255)); // Light blue background
        aiSuggestionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel aiTitleLabel = new JLabel("AI Assistant");
        aiTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel aiSuggestion = new JLabel("Enter a transaction description for AI categorization");
        aiSuggestion.setForeground(Color.GRAY);
        
        // Create a panel for the AI title with better layout
        JPanel aiTitlePanel = new JPanel();
        aiTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        aiTitlePanel.setBackground(new Color(240, 248, 255));
        aiTitlePanel.add(aiTitleLabel);
        
        // Create a panel for the AI suggestion text
        JPanel aiSuggestionTextPanel = new JPanel();
        aiSuggestionTextPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        aiSuggestionTextPanel.setBackground(new Color(240, 248, 255));
        aiSuggestionTextPanel.add(aiSuggestion);
        
        JPanel aiContentPanel = new JPanel();
        aiContentPanel.setLayout(new BoxLayout(aiContentPanel, BoxLayout.Y_AXIS));
        aiContentPanel.setBackground(new Color(240, 248, 255));
        aiContentPanel.add(aiTitlePanel);
        aiContentPanel.add(Box.createVerticalStrut(5));
        aiContentPanel.add(aiSuggestionTextPanel);
        
        aiSuggestionPanel.add(aiContentPanel, BorderLayout.CENTER);
        
        // Submit button
        JButton submitButton = new JButton("Add Transaction");
        submitButton.setBackground(PRIMARY_BLUE);
        submitButton.setForeground(Color.WHITE);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        
        // Add action listener to generate AI suggestion when fields are filled or when "AI Categorize" is selected
        ActionListener suggestListener = e -> {
            if (!descField.getText().isEmpty()) {
                String description = descField.getText().toLowerCase();
                
                // Check if "AI Categorize" is selected
                if (categoryCombo.getSelectedItem().equals("AI Categorize")) {
                    // Call to Deepseek API would go here - for now simulating with basic logic
                    aiSuggestion.setText("Analyzing transaction with Deepseek AI...");
                    
                    // Simulate API call with improved categorization logic
                    // In a real implementation, this would be replaced with an actual API call
                    simulateDeepseekCategorization(description, categoryCombo, aiSuggestion, categories);
                } else if (description.contains("grocery") || description.contains("food") || description.contains("restaurant")) {
                    aiSuggestion.setText("AI suggested category: Food");
                    categoryCombo.setSelectedItem("Food");
                } else if (description.contains("gas") || description.contains("uber") || description.contains("taxi")) {
                    aiSuggestion.setText("AI suggested category: Transportation");
                    categoryCombo.setSelectedItem("Transportation");
                } else if (description.contains("rent") || description.contains("mortgage")) {
                    aiSuggestion.setText("AI suggested category: Housing");
                    categoryCombo.setSelectedItem("Housing");
                } else if (description.contains("movie") || description.contains("netflix") || description.contains("game")) {
                    aiSuggestion.setText("AI suggested category: Entertainment");
                    categoryCombo.setSelectedItem("Entertainment");
                } else if (description.contains("hongbao") || description.contains("gift") || description.contains("new year")) {
                    aiSuggestion.setText("AI suggested category: Chinese New Year");
                    categoryCombo.setSelectedItem("Chinese New Year");
                } else if (categoryCombo.getSelectedItem().equals("Select Category")) {
                    aiSuggestion.setText("Please select a category or use AI categorization");
                }
            }
        };
        
        // Add listener to detect AI Categorize selection
        categoryCombo.addActionListener(e -> {
            if (categoryCombo.getSelectedItem().equals("AI Categorize") && !descField.getText().isEmpty()) {
                suggestListener.actionPerformed(null);
            }
        });
        
        descField.addActionListener(suggestListener);
        descField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                suggestListener.actionPerformed(null);
            }
        });
        
        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(descField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(categoryLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(aiSuggestionPanel, gbc);
        
        // Create a button panel to center the submit button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);
        
        // Create a panel to hold instructions and the form
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(headerLabel, BorderLayout.NORTH);
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Enhanced instructions
        JLabel instructionsLabel = new JLabel("<html><p>Enter your transaction details. Our AI will suggest a category based on the description.</p>" +
                "<p>Select \"AI Categorize\" from the dropdown for intelligent categorization.</p></html>");
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        contentPanel.add(instructionsLabel, BorderLayout.SOUTH);
        
        manualEntryPanel.add(contentPanel, BorderLayout.NORTH);
        
        this.contentPanel.add(manualEntryPanel, "manualEntry");
        
        submitButton.addActionListener(e -> {
            try {
                // Update date format to YYYY/MM/DD
                LocalDate date = LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String description = descField.getText();
                String category = (String) categoryCombo.getSelectedItem();
                
                // Validate category selection
                if (category.equals("Select Category") || category.equals("AI Categorize")) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a specific category for the transaction.", 
                        "Invalid Category", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                double amount = Double.parseDouble(amountField.getText());
                
                Transaction transaction = new Transaction(date, description, category, amount);
                TransactionManager.saveTransaction(transaction);
                
                // Clear fields
                dateField.setText("yyyy-MM-dd");
                descField.setText("");
                amountField.setText("");
                categoryCombo.setSelectedIndex(0);
                
                // Show success message
                JOptionPane.showMessageDialog(this, 
                    "Transaction saved successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                // Refresh the transactions display
                refreshTransactionsDisplay();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving transaction: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Uses DeepseekAPI to categorize transactions
     */
    private void simulateDeepseekCategorization(String description, JComboBox<String> categoryCombo, JLabel aiSuggestion, String[] categories) {
        // Create a list of valid categories (excluding "Select Category" and "AI Categorize")
        String[] validCategories = new String[categories.length - 2];
        System.arraycopy(categories, 2, validCategories, 0, categories.length - 2);
        
        // Update UI to show processing state
        SwingUtilities.invokeLater(() -> {
            aiSuggestion.setText("Processing with Deepseek AI...");
        });
        
        // Call DeepseekAPI to categorize the transaction
        DeepSeekAPI.categorizeTransaction(description, validCategories)
            .thenAccept(result -> {
                SwingUtilities.invokeLater(() -> {
                    // Verify the result is one of our predefined categories
                    boolean isValidCategory = false;
                    for (String category : validCategories) {
                        if (category.equals(result)) {
                            isValidCategory = true;
                            break;
                        }
                    }
                    
                    // If somehow we got an invalid category, default to "Other"
                    String finalCategory = isValidCategory ? result : "Other";
                    
                    // Update the UI with the result
                    aiSuggestion.setText("Deepseek AI categorized as: " + finalCategory);
                    categoryCombo.setSelectedItem(finalCategory);
                });
            })
            .exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    aiSuggestion.setText("Error during AI categorization. Please try again.");
                    categoryCombo.setSelectedItem("Other");
                });
                return null;
            });
    }
    
    /**
     * Creates the import transactions panel
     */
    private void createImportPanel() {
        JPanel importPanel = new JPanel();
        importPanel.setBackground(Color.WHITE);
        importPanel.setLayout(new BorderLayout(10, 10));
        importPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Import Transactions from CSV");
        headerLabel.setFont(SUBHEADER_FONT);
        
        // Enhanced Instructions
        JLabel instructionsLabel = new JLabel("<html><p>Import transactions from a CSV file obtained from your bank or financial institution.</p>"
                + "<p><b>Supported formats:</b> CSV (comma, semicolon, or tab separated)</p>"
                + "<p><b>Required columns (can be in any order):</b> Date, Description, Category, Amount</p>"
                + "<p><b>Supported date formats:</b> yyyy-MM-dd, yyyy/MM/dd, MM/dd/yyyy, dd/MM/yyyy, yyyy.MM.dd, etc.</p>"
                + "<p>Our system will attempt to automatically detect the file format and column headers.</p></html>");
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // File selection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(Color.WHITE);
        
        JTextField filePathField = new JTextField(35); // Increased size
        filePathField.setEditable(false);
        
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".csv");
                }
                
                @Override
                public String getDescription() {
                    return "CSV Files (*.csv)";
                }
            });
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            
            try {
                int result = fileChooser.showOpenDialog(importPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    filePathField.setText(fileChooser.getSelectedFile().getPath());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    importPanel, 
                    "无法打开文件选择器: " + ex.getMessage(),
                    "文件选择错误", 
                    JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });
        
        // Add Generic Sample CSV download button
        JButton genericSampleButton = new JButton("Download Sample CSV");
        genericSampleButton.setToolTipText("Download a sample CSV file showing the expected format");
        genericSampleButton.setBackground(new Color(224, 224, 224));
        genericSampleButton.setForeground(Color.BLACK);
        genericSampleButton.setFocusPainted(false);
        genericSampleButton.addActionListener(e -> downloadGenericSampleCsv());
        selectionPanel.add(genericSampleButton);
        
        selectionPanel.add(filePathField);
        selectionPanel.add(browseButton);
        
        // AI settings panel (remains the same)
        JPanel aiPanel = new JPanel();
        aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
        aiPanel.setBackground(new Color(240, 248, 255));
        aiPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        aiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel aiSettingsLabel = new JLabel("AI Categorization Settings");
        aiSettingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        aiSettingsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox autoCategorizeBox = new JCheckBox("Automatically categorize transactions using AI");
        autoCategorizeBox.setSelected(true);
        autoCategorizeBox.setBackground(new Color(240, 248, 255));
        autoCategorizeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox reviewBeforeSaveBox = new JCheckBox("Review transactions before saving (Recommended)");
        reviewBeforeSaveBox.setSelected(true);
        reviewBeforeSaveBox.setBackground(new Color(240, 248, 255));
        reviewBeforeSaveBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox detectSeasonalBox = new JCheckBox("Detect seasonal spending patterns (e.g. Chinese New Year)");
        detectSeasonalBox.setSelected(true);
        detectSeasonalBox.setBackground(new Color(240, 248, 255));
        detectSeasonalBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        aiPanel.add(aiSettingsLabel);
        aiPanel.add(Box.createVerticalStrut(5));
        aiPanel.add(autoCategorizeBox);
        aiPanel.add(Box.createVerticalStrut(2));
        aiPanel.add(reviewBeforeSaveBox);
        aiPanel.add(Box.createVerticalStrut(2));
        aiPanel.add(detectSeasonalBox);
        
        // Import button
        JButton importButton = new JButton("Import Transactions");
        importButton.setBackground(PRIMARY_BLUE);
        importButton.setForeground(Color.WHITE);
        importButton.setOpaque(true);
        importButton.setBorderPainted(false);
        importButton.setFocusPainted(false);
        importButton.setEnabled(false); // Enabled when file is selected
        
        // Enable import button only when file path is filled
        filePathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { checkFields(); }
            @Override
            public void removeUpdate(DocumentEvent e) { checkFields(); }
            @Override
            public void changedUpdate(DocumentEvent e) { checkFields(); }
            
            private void checkFields() {
                importButton.setEnabled(!filePathField.getText().isEmpty());
            }
        });
        
        // Create a panel to hold the main components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Align components to the left
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        aiPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to the content panel
        contentPanel.add(headerLabel);
        contentPanel.add(instructionsLabel);
        contentPanel.add(selectionPanel);
        // Removed bankPanel here
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(aiPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(importButton);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(buttonPanel);
        
        // Add seasonal spending notice (remains the same)
        JPanel seasonalPanel = new JPanel();
        seasonalPanel.setLayout(new BorderLayout());
        seasonalPanel.setBackground(new Color(255, 240, 240));
        seasonalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 153, 153), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        seasonalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        seasonalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Set max height
        
        JLabel seasonalLabel = new JLabel("<html><b>Chinese New Year Spending Notice:</b> " +
                "Our AI has detected higher spending patterns typical for Chinese New Year season. " +
                "Consider reviewing your budget.</html>");
        
        JPanel buttonContainerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainerPanel.setBackground(new Color(255, 240, 240));
        
        JButton adjustButton = new JButton("Adjust Budget");
        adjustButton.setBackground(new Color(229, 57, 53));
        adjustButton.setForeground(Color.WHITE);
        adjustButton.setOpaque(true);
        adjustButton.setBorderPainted(false);
        
        JButton remindButton = new JButton("Remind Later");
        remindButton.setBackground(new Color(200, 200, 200));
        remindButton.setForeground(Color.BLACK);
        remindButton.setOpaque(true);
        remindButton.setBorderPainted(false);
        
        buttonContainerPanel.add(adjustButton);
        buttonContainerPanel.add(remindButton);
        
        seasonalPanel.add(seasonalLabel, BorderLayout.CENTER);
        seasonalPanel.add(buttonContainerPanel, BorderLayout.SOUTH);
        
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(seasonalPanel);
        
        // Add filler to push content to the top
        contentPanel.add(Box.createVerticalGlue());
        
        importPanel.add(contentPanel, BorderLayout.NORTH);
        
        // Preview area (remains the same for now, might be enhanced later)
        JLabel previewLabel = new JLabel("Transaction Preview (after import)");
        previewLabel.setFont(SUBHEADER_FONT);
        previewLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(Color.WHITE);
        previewPanel.add(previewLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"Date", "Description", "Category", "Amount", "AI Confidence"};
        Object[][] data = {};
        JTable previewTable = new JTable(data, columnNames);
        JScrollPane tableScroll = new JScrollPane(previewTable);
        tableScroll.setPreferredSize(new Dimension(600, 150));
        
        previewPanel.add(tableScroll, BorderLayout.CENTER);
        
        // importPanel.add(previewPanel, BorderLayout.CENTER); // Add preview later if needed
        
        this.contentPanel.add(importPanel, "import");
        
        // Import button action listener (remains mostly the same, no bank dependency)
        importButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                try {
                    JDialog processingDialog = new JDialog(this, "导入中...", false);
                    JLabel processingLabel = new JLabel("正在导入交易数据，请稍候...");
                    processingLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    processingDialog.add(processingLabel);
                    processingDialog.pack();
                    processingDialog.setLocationRelativeTo(this);
                    
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        private String errorMessage = null;
                        private boolean hasDetailedError = false;
                        
                        @Override
                        protected Void doInBackground() {
                            try {
                                // Import directly using the format detection in TransactionManager
                                TransactionManager.importFromCSV(filePath);
                            } catch (TransactionManager.ImportException ex) {
                                errorMessage = ex.getMessage();
                                hasDetailedError = true;
                                ex.printStackTrace();
                            } catch (Exception ex) {
                                errorMessage = ex.getMessage();
                                ex.printStackTrace();
                            }
                            return null;
                        }
                        
                        @Override
                        protected void done() {
                            processingDialog.dispose();
                            
                            if (errorMessage == null) {
                                JOptionPane.showMessageDialog(
                                    BillingView.this, 
                                    "交易数据导入成功！", 
                                    "导入成功", 
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                refreshTransactionsDisplay();
                            } else {
                                if (hasDetailedError && errorMessage.contains("Import completed with errors")) {
                                    showDetailedErrorDialog(errorMessage);
                                } else {
                                    JOptionPane.showMessageDialog(
                                        BillingView.this, 
                                        "导入交易时出错: " + errorMessage, 
                                        "导入错误", 
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        }
                    };
                    
                    worker.execute();
                    processingDialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                        this, 
                        "导入交易时出错: " + ex.getMessage(), 
                        "导入错误", 
                        JOptionPane.ERROR_MESSAGE
                    );
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "请先选择CSV文件",
                    "文件未选择",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
    }
    
    /**
     * Displays a detailed error dialog for CSV import errors
     * @param errorMessage The detailed error message from ImportException
     */
    private void showDetailedErrorDialog(String errorMessage) {
        // Create a custom dialog for showing detailed import errors
        JDialog errorDialog = new JDialog(this, "Import Results", true);
        errorDialog.setLayout(new BorderLayout());
        errorDialog.setSize(600, 400);
        errorDialog.setLocationRelativeTo(this);
        
        // Create text area for errors with scroll capability
        JTextArea errorArea = new JTextArea(errorMessage);
        errorArea.setEditable(false);
        errorArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        errorArea.setLineWrap(true);
        errorArea.setWrapStyleWord(true);
        errorArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(errorArea);
        
        // Add header panel with icon and title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel warningIcon = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        JLabel titleLabel = new JLabel("Import completed with errors");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        headerPanel.add(warningIcon, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> errorDialog.dispose());
        
        JButton viewHelpButton = new JButton("View Help");
        viewHelpButton.addActionListener(e -> {
            // Open help for CSV import format guidelines
            JOptionPane.showMessageDialog(errorDialog,
                "CSV Import Guidelines:\n\n" +
                "1. Ensure your CSV file has headers for Date, Description, Category, and Amount\n" +
                "2. Supported date formats: YYYY-MM-DD, YYYY/MM/DD, MM/DD/YYYY\n" +
                "3. Amount should be a number, with optional currency symbols\n" +
                "4. For best results, export CSV files directly from your banking app\n\n" +
                "We support formats from: Bank of China, ICBC, China Construction Bank, and many others.",
                "CSV Import Help",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(viewHelpButton);
        buttonPanel.add(closeButton);
        
        // Add everything to dialog
        errorDialog.add(headerPanel, BorderLayout.NORTH);
        errorDialog.add(scrollPane, BorderLayout.CENTER);
        errorDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        errorDialog.setVisible(true);
    }
    
    /**
     * Handles the download action for the generic sample CSV file.
     */
    private void downloadGenericSampleCsv() {
        // Define standard headers and a common date format
        String[] headers = {"Date", "Description", "Category", "Amount"};
        String dateFormat = "yyyy-MM-dd"; // Use a common, unambiguous format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        LocalDate today = LocalDate.now();
        
        // Create the CSV content
        StringBuilder csvContent = new StringBuilder();
        csvContent.append(String.join(",", headers)).append("\n");
        
        // Add sample rows
        csvContent.append(today.format(formatter)).append(",");
        csvContent.append("\"Grocery Shopping, Local Market\","); // Fixed: Comma inside quotes
        csvContent.append("Food,");
        csvContent.append("150.75\n");
        
        csvContent.append(today.minusDays(2).format(formatter)).append(",");
        csvContent.append("Monthly Rent Payment,");
        csvContent.append("Housing,");
        csvContent.append("\"3,500.00\"\n"); // Example with quotes and comma
        
        csvContent.append(today.minusDays(5).format(formatter)).append(",");
        csvContent.append("DiDi Ride,");
        csvContent.append("Transportation,");
        csvContent.append("35.50\n");
        
        csvContent.append(today.minusDays(7).format(formatter)).append(",");
        csvContent.append("Netflix Subscription,");
        csvContent.append("Entertainment,");
        csvContent.append("49.99\n");
        
        csvContent.append(today.minusDays(10).format(formatter)).append(",");
        csvContent.append("Red Envelope Gift,");
        csvContent.append("Chinese New Year,");
        csvContent.append("888.00\n");

        // Use JFileChooser to let the user choose where to save the file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sample CSV");
        fileChooser.setSelectedFile(new File("sample_transactions.csv"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Ensure the file has a .csv extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                fileToSave = new File(filePath + ".csv");
            }
            
            // Write the content to the selected file
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(csvContent.toString());
                JOptionPane.showMessageDialog(this,
                    "Sample CSV file saved successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Sample Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving sample CSV file: " + ex.getMessage(),
                    "File Save Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Adds a transaction item to the list display
     * @param panel The panel to add the transaction to
     * @param date The date of the transaction
     * @param description The description of the transaction
     * @param category The category of the transaction
     * @param amount The amount of the transaction
     */
    private void addTransactionItem(JPanel panel, String date, String description, String category, double amount) {
        // Get current user's currency preference
        User currentUser = UserManager.getInstance().getCurrentUser();
        String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
        
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(CONTENT_FONT);
        descriptionLabel.setForeground(DARK_GRAY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        categoryLabel.setForeground(new Color(150, 150, 150));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createVerticalStrut(3));
        detailsPanel.add(categoryLabel);
        
        // Convert amount to user's preferred currency
        double convertedAmount = currencyManager.convert(amount, CurrencyManager.CNY, userCurrency);
        
        // Format amount according to user's currency
        String formattedAmount = currencyManager.format(convertedAmount, userCurrency);
        
        JLabel amountLabel = new JLabel(formattedAmount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(amount < 0 ? new Color(231, 76, 60) : new Color(46, 204, 113));
        
        itemPanel.add(detailsPanel, BorderLayout.WEST);
        itemPanel.add(amountLabel, BorderLayout.EAST);
        
        panel.add(itemPanel);
        panel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Refreshes the transactions display to show current data
     */
    private void refreshTransactionsDisplay() {
        if (transactionsPanel != null) {
            transactionsPanel.removeAll();
            
            // Get transactions
            List<Transaction> transactions = TransactionManager.loadTransactions();
            
            if (transactions.isEmpty()) {
                JLabel noTransactionsLabel = new JLabel("No transactions found");
                noTransactionsLabel.setFont(CONTENT_FONT);
                noTransactionsLabel.setForeground(DARK_GRAY);
                noTransactionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                transactionsPanel.add(noTransactionsLabel);
            } else {
                // Add each transaction to the list
                for (Transaction transaction : transactions) {
                    addTransactionItem(
                        transactionsPanel,
                        transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        transaction.getDescription(),
                        transaction.getCategory(),
                        transaction.getAmount()
                    );
                }
            }
            
            transactionsPanel.revalidate();
            transactionsPanel.repaint();
        }
    }
    
    /**
     * Main method to run the application
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new BillingView());
    }
} 