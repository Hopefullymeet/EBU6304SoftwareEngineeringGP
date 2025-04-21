package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.SessionManager;
import model.UserManager;
import model.Transaction;
import model.TransactionManager;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.User;
import model.CurrencyManager;

/**
 * BudgetView - The budget management screen.
 * This class displays budget information and tracking.
 */
public class BudgetView extends JFrame {
    
    // UI Components
    private JPanel contentPanel;
    
    // Sidebar items
    private JPanel accountPanel;
    private JPanel billingPanel;
    private JPanel budgetPanel;
    private JPanel insightsPanel;
    private JPanel helpCenterPanel;
    private JPanel financialAdvisorPanel;
    
    // Budget sub-items container
    private JPanel budgetSubItems;
    private boolean budgetExpanded = true;
    
    // Content cards
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private final String BUDGET_OVERVIEW_PANEL = "BudgetOverviewPanel";
    private final String AI_INSIGHTS_PANEL = "AIInsightsPanel";
    private final String BUDGET_SETTINGS_PANEL = "BudgetSettingsPanel";
    
    // Budget insights panel
    private BudgetInsightsPanel budgetInsightsPanel;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    // Managers
    private CurrencyManager currencyManager;
    
    private JComboBox<String> currencySelector;
    private User currentUser;
    
    /**
     * Constructor for the BudgetView
     */
    public BudgetView() {
        currencyManager = CurrencyManager.getInstance();

        setTitle("Budget");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        User currentUser = UserManager.getInstance().getCurrentUser();

        createHeader();
        createSidebar();
        
        // Create card layout for different content panels
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Color.WHITE);
        
        // Add different content panels to the card layout
        createBudgetContent();
        createBudgetInsightsPanel();
        createBudgetSettingsPanel();
        
        add(cardsPanel, BorderLayout.CENTER);
        
        // Default view is budget overview panel
        cardLayout.show(cardsPanel, BUDGET_OVERVIEW_PANEL);
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Start session monitoring
        SessionManager.getInstance().startSession(this);
    }
    
    /**
     * Creates the header panel with title and logout button
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Budget");
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
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop session monitoring
                SessionManager.getInstance().stopSession();
                
                // Log out the user
                UserManager.getInstance().logout();
                
                dispose(); // Close current window
                new LoginView(); // Return to login screen
            }
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
        billingPanel = createSidebarItem("Billing & Subscriptions", false, false);
        budgetPanel = createSidebarItem("Budget", false, true);
        helpCenterPanel = createSidebarItem("Help Center", false, false);
        financialAdvisorPanel = createSidebarItem("Financial Advisor", false, false);
        
        // Create Budget sub-items container
        budgetSubItems = new JPanel();
        budgetSubItems.setLayout(new BoxLayout(budgetSubItems, BoxLayout.Y_AXIS));
        budgetSubItems.setBackground(Color.WHITE);
        budgetSubItems.setVisible(budgetExpanded);
        
        // Create Budget sub-items
        JPanel budgetOverviewPanel = createSidebarItem("Budget Overview", true, true);
        JPanel aiInsightsPanel = createSidebarItem("AI Insights", true, false);
        JPanel budgetSettingsPanel = createSidebarItem("Budget Settings", true, false);
        
        // Add click listeners
        accountPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new AccountView();
            }
        });
        
        billingPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new BillingView();
            }
        });
        
        budgetPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                toggleBudgetSubItems();
            }
        });
        
        helpCenterPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new HelpCenterView();
            }
        });
        
        // Add click listener for Financial Advisor
        financialAdvisorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new FinancialAdvisorView();
            }
        });
        
        // Add click listeners for Budget sub-items
        budgetOverviewPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(budgetOverviewPanel);
                cardLayout.show(cardsPanel, BUDGET_OVERVIEW_PANEL);
            }
        });
        
        aiInsightsPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(aiInsightsPanel);
                // Show AI insights panel
                cardLayout.show(cardsPanel, AI_INSIGHTS_PANEL);
                // Update the budget insights data
                updateBudgetInsightsData();
            }
        });
        
        budgetSettingsPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(budgetSettingsPanel);
                // Show budget settings panel
                cardLayout.show(cardsPanel, BUDGET_SETTINGS_PANEL);
            }
        });
        
        // Add sub-items to container
        budgetSubItems.add(budgetOverviewPanel);
        budgetSubItems.add(Box.createVerticalStrut(5));
        budgetSubItems.add(aiInsightsPanel);
        budgetSubItems.add(Box.createVerticalStrut(5));
        budgetSubItems.add(budgetSettingsPanel);
        
        // Add sidebar items to panel
        sidebarPanel.add(accountPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billingPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetPanel);
        sidebarPanel.add(budgetSubItems);
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
     * Toggle the visibility of Budget sub-items
     */
    private void toggleBudgetSubItems() {
        budgetExpanded = !budgetExpanded;
        budgetSubItems.setVisible(budgetExpanded);
        
        // Add arrow indicator to Budget item
        JLabel budgetLabel = (JLabel) ((JPanel) budgetPanel.getComponent(0)).getComponent(0);
        budgetLabel.setText("Budget " + (budgetExpanded ? "▼" : "▶"));
        
        // Refresh the UI
        budgetPanel.revalidate();
        budgetPanel.repaint();
    }
    
    /**
     * Sets the active sidebar item by changing its background color
     * @param activePanel The panel to set as active
     */
    private void setActiveSidebarItem(JPanel activePanel) {
        // Collect all sub-items
        Component[] subItems = budgetSubItems.getComponents();
        
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
        if (text.equals("Budget")) {
            displayText = text + " " + (budgetExpanded ? "▼" : "▶");
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
     * Creates the budget content panel
     */
    private void createBudgetContent() {
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create a container panel with vertical BoxLayout
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBackground(Color.WHITE);
        
        // Set fixed width for the container to prevent rendering issues
        containerPanel.setPreferredSize(new Dimension(700, 1200));
        
        // Budget details panel
        JPanel budgetDetails = new JPanel();
        budgetDetails.setLayout(new BoxLayout(budgetDetails, BoxLayout.Y_AXIS));
        budgetDetails.setBackground(Color.WHITE);
        budgetDetails.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // AI Insights Panel - Add at the top
        JPanel insightsPanel = createAIInsightsPanel();
        insightsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        insightsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        // Load transaction data for summary calculations
        List<Transaction> transactions = TransactionManager.loadTransactions();
        double totalSpent = 0.0;
        Map<String, Double> categorySpending = new HashMap<>();
        
        // Calculate totals
        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();
            totalSpent += amount;
            
            String category = transaction.getCategory();
            categorySpending.put(category, 
                categorySpending.getOrDefault(category, 0.0) + amount);
        }
        
        // If no transactions, use sample data
        if (transactions.isEmpty()) {
            // Sample data matching the one in BudgetInsightsPanel
            totalSpent = 3240.75;
            categorySpending.put("Housing", 1500.0);
            categorySpending.put("Food & Dining", 650.25);
            categorySpending.put("Transportation", 420.50);
            categorySpending.put("Utilities", 340.0);
            categorySpending.put("Entertainment", 200.0);
            categorySpending.put("Other", 130.0);
        }
        
        // Budget values - Use same totalBudget as in BudgetInsightsPanel
        double totalBudget = 5000.00;
        double remaining = totalBudget - totalSpent;
        
        // Budget summary section
        JLabel summaryLabel = new JLabel("Budget Summary");
        summaryLabel.setFont(SUBHEADER_FONT);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(new EmptyBorder(15, 0, 30, 0));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        // Add summary cards with actual data
        addSummaryCard(summaryPanel, "Total Budget", String.format("¥%.2f", totalBudget), new Color(52, 152, 219));
        addSummaryCard(summaryPanel, "Spent", String.format("¥%.2f", totalSpent), new Color(46, 204, 113));
        addSummaryCard(summaryPanel, "Remaining", String.format("¥%.2f", remaining), new Color(155, 89, 182));
        
        // Category breakdown section
        JLabel categoriesLabel = new JLabel("Category Breakdown");
        categoriesLabel.setFont(SUBHEADER_FONT);
        categoriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(Color.WHITE);
        categoriesPanel.setBorder(new EmptyBorder(15, 0, 30, 0));
        categoriesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Budget allocations by category - could be refined with actual budget allocations
        Map<String, Double> categoryBudgets = new HashMap<>();
        
        // Define category colors for consistency
        Map<String, Color> categoryColors = new HashMap<>();
        categoryColors.put("Housing", new Color(41, 128, 185));
        categoryColors.put("Food & Dining", new Color(39, 174, 96));
        categoryColors.put("Transportation", new Color(142, 68, 173));
        categoryColors.put("Entertainment", new Color(243, 156, 18));
        categoryColors.put("Shopping", new Color(231, 76, 60));
        categoryColors.put("Utilities", new Color(52, 73, 94));
        categoryColors.put("Chinese New Year", new Color(220, 20, 60));
        categoryColors.put("Other", new Color(149, 165, 166));
        categoryColors.put("Medical", new Color(41, 128, 185));
        categoryColors.put("Education", new Color(39, 174, 96));
        categoryColors.put("Travel", new Color(142, 68, 173));
        
        // Create simple budget allocations based on typical percentages
        categoryBudgets.put("Housing", totalBudget * 0.3); // 30% of budget
        categoryBudgets.put("Food & Dining", totalBudget * 0.2); // 20% of budget
        categoryBudgets.put("Transportation", totalBudget * 0.1); // 10% of budget
        categoryBudgets.put("Entertainment", totalBudget * 0.1); // 10% of budget
        categoryBudgets.put("Utilities", totalBudget * 0.1); // 10% of budget
        categoryBudgets.put("Shopping", totalBudget * 0.1); // 10% of budget
        categoryBudgets.put("Chinese New Year", totalBudget * 0.05); // 5% of budget
        categoryBudgets.put("Other", totalBudget * 0.05); // 5% of budget
        
        // Add any categories that exist in transactions but not in budgets
        for (String category : categorySpending.keySet()) {
            if (!categoryBudgets.containsKey(category)) {
                categoryBudgets.put(category, totalBudget * 0.05); // Default 5% allocation
            }
            
            if (!categoryColors.containsKey(category)) {
                // Generate a color if not defined
                categoryColors.put(category, new Color(149, 165, 166)); // Default gray
            }
        }
        
        // Add category items based on actual spending
        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            String category = entry.getKey();
            double spent = entry.getValue();
            double budget = categoryBudgets.getOrDefault(category, totalBudget * 0.05);
            Color color = categoryColors.getOrDefault(category, new Color(149, 165, 166));
            
            addCategoryItem(categoriesPanel, category, budget, spent, color);
        }
        
        // Recent transactions section
        JLabel transactionsLabel = new JLabel("Recent Transactions");
        transactionsLabel.setFont(SUBHEADER_FONT);
        transactionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create table model for recent transactions
        String[] columnNames = {"Date", "Description", "Category", "Amount"};
        Object[][] data;
        
        if (transactions.isEmpty()) {
            // Sample data if no transactions
            data = new Object[][] {
                {"2024-01-20", "Grocery Store", "Food & Dining", "-¥85.43"},
                {"2024-01-18", "Gas Station", "Transportation", "-¥45.75"},
                {"2024-01-15", "Coffee Shop", "Food & Dining", "-¥4.50"},
                {"2024-01-14", "Online Store", "Shopping", "-¥67.89"},
                {"2024-01-12", "Electric Bill", "Utilities", "-¥98.76"}
            };
        } else {
            // Get actual transactions (up to 5 most recent)
            int count = Math.min(transactions.size(), 5);
            data = new Object[count][4];
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (int i = 0; i < count; i++) {
                Transaction transaction = transactions.get(i);
                data[i][0] = transaction.getDate().format(formatter);
                data[i][1] = transaction.getDescription();
                data[i][2] = transaction.getCategory();
                data[i][3] = String.format("-¥%.2f", transaction.getAmount());
            }
        }
        
        JTable transactionsTable = new JTable(data, columnNames);
        transactionsTable.setFont(CONTENT_FONT);
        transactionsTable.setRowHeight(25);
        
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        tableScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        tableScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to budget details panel
        budgetDetails.add(summaryLabel);
        budgetDetails.add(Box.createVerticalStrut(10));
        budgetDetails.add(summaryPanel);
        budgetDetails.add(Box.createVerticalStrut(20));
        budgetDetails.add(categoriesLabel);
        budgetDetails.add(Box.createVerticalStrut(10));
        budgetDetails.add(categoriesPanel);
        budgetDetails.add(Box.createVerticalStrut(20));
        budgetDetails.add(transactionsLabel);
        budgetDetails.add(Box.createVerticalStrut(10));
        budgetDetails.add(tableScrollPane);
        
        // Add buttons for budget actions
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton editBudgetButton = new JButton("Edit Budget");
        editBudgetButton.setBackground(PRIMARY_BLUE);
        editBudgetButton.setForeground(Color.WHITE);
        editBudgetButton.setFont(CONTENT_FONT);
        editBudgetButton.setFocusPainted(false);
        editBudgetButton.setOpaque(true);
        editBudgetButton.setBorderPainted(false);
        
        JButton addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setFont(CONTENT_FONT);
        
        actionsPanel.add(editBudgetButton);
        actionsPanel.add(Box.createHorizontalStrut(10));
        actionsPanel.add(addTransactionButton);
        
        // Add all elements to the container panel
        containerPanel.add(insightsPanel);
        containerPanel.add(Box.createVerticalStrut(20));
        containerPanel.add(budgetDetails);
        containerPanel.add(Box.createVerticalStrut(20));
        
        // Add scroll pane for container panel
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to main panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add to cards panel
        cardsPanel.add(contentPanel, BUDGET_OVERVIEW_PANEL);
    }
    
    /**
     * Creates the AI Insights panel with spending suggestions
     */
    private JPanel createAIInsightsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(173, 216, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(700, 150));
        panel.setMinimumSize(new Dimension(400, 100));
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 248, 255));
        
        JLabel aiIconLabel = new JLabel("🤖");
        aiIconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        JLabel titleLabel = new JLabel("AI Budget Insights");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        headerPanel.add(aiIconLabel);
        headerPanel.add(titleLabel);
        
        JPanel insightsContentPanel = new JPanel();
        insightsContentPanel.setLayout(new BoxLayout(insightsContentPanel, BoxLayout.Y_AXIS));
        insightsContentPanel.setBackground(new Color(240, 248, 255));
        
        // Calculate insights based on actual data
        List<Transaction> transactions = TransactionManager.loadTransactions();
        double totalSpent = 0.0;
        for (Transaction transaction : transactions) {
            totalSpent += transaction.getAmount();
        }
        
        // Calculate sample insights
        double foodPercentage = 0.0;
        double housingPercentage = 0.0;
        
        for (Transaction transaction : transactions) {
            if (transaction.getCategory().equals("Food & Dining")) {
                foodPercentage += transaction.getAmount() / totalSpent * 100;
            } else if (transaction.getCategory().equals("Housing")) {
                housingPercentage += transaction.getAmount() / totalSpent * 100;
            }
        }
        
        String insightsHTML = "<html>";
        if (transactions.isEmpty()) {
            // Default insights if no transactions
            insightsHTML += "<p>• Based on your spending patterns, you could save <b>¥350/month</b> by reducing dining out expenses.</p>" +
                     "<p>• Your utility bills are <b>18% lower</b> than similar households in your region.</p>" +
                     "<p>• <span style='color:#c0392b;'><b>Alert:</b> Your remaining budget is ¥4975.00. Click 'AI Insights' for personalized recommendations.</span></p>";
        } else {
            // Dynamic insights based on actual data
            double totalBudget = 5000.00;
            double remaining = totalBudget - totalSpent;
            
            insightsHTML += "<p>• You've spent <b>" + String.format("%.1f", (totalSpent/totalBudget*100)) + 
                    "%</b> of your monthly budget with <b>¥" + String.format("%.2f", remaining) + "</b> remaining.</p>";
            
            if (foodPercentage > 0) {
                insightsHTML += "<p>• <b>" + String.format("%.1f", foodPercentage) + 
                        "%</b> of your spending is on food & dining this month.</p>";
            }
            
            insightsHTML += "<p>• <span style='color:#c0392b;'><b>Alert:</b> Click 'Generate' in AI Insights for personalized recommendations based on your spending patterns.</span></p>";
        }
        insightsHTML += "</html>";
        
        JLabel insightsText = new JLabel(insightsHTML);
        insightsText.setFont(CONTENT_FONT);
        insightsText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        insightsContentPanel.add(insightsText);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(insightsContentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the seasonal budget planning panel
     */
    private JPanel createSeasonalBudgetPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(255, 240, 240)); // Light red for Chinese New Year
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 153, 153), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(700, 250));
        panel.setMinimumSize(new Dimension(400, 200));
        
        JLabel titleLabel = new JLabel("Chinese New Year Budget Planning");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(180, 0, 0));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 240, 240));
        
        // Create a table for budget allocation
        String[] columnNames = {"Expense Type", "Last Year", "AI Recommendation", "Your Budget"};
        Object[][] data = {
            {"Red Envelopes (Hongbao)", "¥300", "¥330", "¥350"},
            {"Family Dinner", "¥250", "¥275", "¥300"},
            {"Decorations", "¥50", "¥50", "¥50"},
            {"Travel", "¥100", "¥110", "¥100"}
        };
        
        JTable budgetTable = new JTable(data, columnNames);
        budgetTable.setRowHeight(25);
        budgetTable.setBackground(new Color(255, 240, 240));
        budgetTable.setGridColor(new Color(255, 200, 200));
        
        JScrollPane tableScrollPane = new JScrollPane(budgetTable);
        tableScrollPane.setPreferredSize(new Dimension(500, 120));
        tableScrollPane.setBackground(new Color(255, 240, 240));
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        contentPanel.add(tableScrollPane);
        
        JButton savePlanButton = new JButton("Save Chinese New Year Budget Plan");
        savePlanButton.setBackground(new Color(220, 20, 60));
        savePlanButton.setForeground(Color.WHITE);
        savePlanButton.setOpaque(true);
        savePlanButton.setBorderPainted(false);
        savePlanButton.setFocusPainted(false);
        savePlanButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 240, 240));
        buttonPanel.add(savePlanButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Adds a summary card to the panel
     * @param panel The panel to add the card to
     * @param title The card title
     * @param amount The amount to display
     * @param color The card color
     */
    private void addSummaryCard(JPanel panel, String title, String amount, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(LIGHT_GRAY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CONTENT_FONT);
        titleLabel.setForeground(DARK_GRAY);
        
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        amountLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);
        
        panel.add(card);
    }
    
    /**
     * Adds a category item with progress bar to the panel
     * @param panel The panel to add the item to
     * @param category The category name
     * @param budget The budget amount
     * @param spent The spent amount
     * @param color The category color
     */
    private void addCategoryItem(JPanel panel, String category, double budget, double spent, Color color) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout(10, 0));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        // Category name
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(CONTENT_FONT);
        categoryLabel.setPreferredSize(new Dimension(150, 25));
        
        // Progress panel
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout(5, 0));
        progressPanel.setBackground(Color.WHITE);
        
        // Calculate percentage - ensure it's within valid range
        int percentage = 0;
        if (budget > 0 && spent >= 0) {
            percentage = (int)((spent / budget) * 100);
            if (percentage < 0) percentage = 0;
            if (percentage > 100) percentage = 100;
        }
        
        // Progress bar with fixed minimum size
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setForeground(color);
        progressBar.setStringPainted(false);
        progressBar.setMinimumSize(new Dimension(50, 20));
        progressBar.setPreferredSize(new Dimension(250, 20));
        
        // Amounts
        JLabel amountsLabel = new JLabel(String.format("¥%.2f / ¥%.2f", spent, budget));
        amountsLabel.setFont(CONTENT_FONT);
        amountsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        amountsLabel.setPreferredSize(new Dimension(120, 25));
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(amountsLabel, BorderLayout.EAST);
        
        itemPanel.add(categoryLabel, BorderLayout.WEST);
        itemPanel.add(progressPanel, BorderLayout.CENTER);
        
        panel.add(itemPanel);
        panel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * Creates the budget insights panel using the BudgetInsightsPanel component
     */
    private void createBudgetInsightsPanel() {
        // Create the budget insights panel
        budgetInsightsPanel = new BudgetInsightsPanel();
        
        // Update it with current budget data
        updateBudgetInsightsData();
        
        // Add to cards panel
        cardsPanel.add(budgetInsightsPanel, AI_INSIGHTS_PANEL);
    }
    
    /**
     * Updates the budget insights panel with current budget data
     */
    private void updateBudgetInsightsData() {
        if (budgetInsightsPanel != null) {
            // Get transaction data
            List<Transaction> transactions = TransactionManager.loadTransactions();
            
            // Calculate total spent amount
            double totalSpent = 0.0;
            Map<String, Double> categoryBreakdown = new HashMap<>();
            
            for (Transaction transaction : transactions) {
                double amount = transaction.getAmount();
                totalSpent += amount;
                
                // Update category breakdown
                String category = transaction.getCategory();
                categoryBreakdown.put(
                    category, 
                    categoryBreakdown.getOrDefault(category, 0.0) + amount
                );
            }
            
            // Get total budget (using sample value from createBudgetContent)
            double totalBudget = 5000.00;
            
            // Update the budget insights panel with this data
            budgetInsightsPanel.updateBudgetData(totalBudget, totalSpent, categoryBreakdown);
        }
    }
    
    /**
     * Creates a simple budget settings panel
     */
    private void createBudgetSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BorderLayout());
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Budget Settings");
        titleLabel.setFont(SUBHEADER_FONT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(Color.WHITE);
        
        // Monthly budget setting
        JLabel budgetLabel = new JLabel("Monthly Budget:");
        budgetLabel.setFont(CONTENT_FONT);
        
        JTextField budgetField = new JTextField("5000.00");
        budgetField.setFont(CONTENT_FONT);
        
        // Auto-categorization setting
        JLabel categorizationLabel = new JLabel("Auto-categorize transactions:");
        categorizationLabel.setFont(CONTENT_FONT);
        
        JCheckBox categorizationCheckbox = new JCheckBox();
        categorizationCheckbox.setSelected(true);
        categorizationCheckbox.setBackground(Color.WHITE);
        
        // Budget alerts setting
        JLabel alertsLabel = new JLabel("Budget alerts:");
        alertsLabel.setFont(CONTENT_FONT);
        
        JCheckBox alertsCheckbox = new JCheckBox();
        alertsCheckbox.setSelected(true);
        alertsCheckbox.setBackground(Color.WHITE);
        
        // Add components to form
        formPanel.add(budgetLabel);
        formPanel.add(budgetField);
        formPanel.add(categorizationLabel);
        formPanel.add(categorizationCheckbox);
        formPanel.add(alertsLabel);
        formPanel.add(alertsCheckbox);
        
        // Save button
        JButton saveButton = new JButton("Save Settings");
        saveButton.setBackground(PRIMARY_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(CONTENT_FONT);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(saveButton);
        
        // Add components to main panel
        settingsPanel.add(titleLabel, BorderLayout.NORTH);
        settingsPanel.add(formPanel, BorderLayout.CENTER);
        settingsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add to cards panel
        cardsPanel.add(settingsPanel, BUDGET_SETTINGS_PANEL);
    }
    
    /**
     * Main method to test the BudgetView
     */
    public static void main(String[] args) {
        // Set the look and feel to the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BudgetView();
            }
        });
    }
} 