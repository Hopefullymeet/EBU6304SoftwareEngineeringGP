package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Constructor for the BudgetView
     */
    public BudgetView() {
        setTitle("Budget");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createHeader();
        createSidebar();
        createBudgetContent();
        
        setLocationRelativeTo(null);
        setVisible(true);
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
                // Show budget overview panel (main panel)
            }
        });
        
        aiInsightsPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(aiInsightsPanel);
                // Show AI insights panel
            }
        });
        
        budgetSettingsPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(budgetSettingsPanel);
                // Show budget settings panel
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
        
        // Add summary cards
        addSummaryCard(summaryPanel, "Total Budget", "¥5,000.00", new Color(52, 152, 219));
        addSummaryCard(summaryPanel, "Spent", "¥3,240.75", new Color(46, 204, 113));
        addSummaryCard(summaryPanel, "Remaining", "¥1,759.25", new Color(155, 89, 182));
        
        // Category breakdown section
        JLabel categoriesLabel = new JLabel("Category Breakdown");
        categoriesLabel.setFont(SUBHEADER_FONT);
        categoriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel categoriesPanel = new JPanel();
        categoriesPanel.setLayout(new BoxLayout(categoriesPanel, BoxLayout.Y_AXIS));
        categoriesPanel.setBackground(Color.WHITE);
        categoriesPanel.setBorder(new EmptyBorder(15, 0, 30, 0));
        categoriesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add category items
        addCategoryItem(categoriesPanel, "Housing", 1500.00, 1500.00, new Color(41, 128, 185));
        addCategoryItem(categoriesPanel, "Food & Dining", 800.00, 650.25, new Color(39, 174, 96));
        addCategoryItem(categoriesPanel, "Transportation", 400.00, 350.50, new Color(142, 68, 173));
        addCategoryItem(categoriesPanel, "Entertainment", 300.00, 275.00, new Color(243, 156, 18));
        addCategoryItem(categoriesPanel, "Shopping", 400.00, 325.00, new Color(231, 76, 60));
        addCategoryItem(categoriesPanel, "Utilities", 350.00, 140.00, new Color(52, 73, 94));
        addCategoryItem(categoriesPanel, "Chinese New Year", 800.00, 0.00, new Color(220, 20, 60));
        
        // Add seasonal budget planning section
        JPanel seasonalPanel = createSeasonalBudgetPanel();
        seasonalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Recent transactions section
        JLabel transactionsLabel = new JLabel("Recent Transactions");
        transactionsLabel.setFont(SUBHEADER_FONT);
        transactionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] columnNames = {"Date", "Description", "Category", "Amount"};
        Object[][] data = {
            {"Jan 20, 2024", "Grocery Store", "Food & Dining", "-¥85.43"},
            {"Jan 18, 2024", "Gas Station", "Transportation", "-¥45.75"},
            {"Jan 15, 2024", "Coffee Shop", "Food & Dining", "-¥4.50"},
            {"Jan 14, 2024", "Online Store", "Shopping", "-¥67.89"},
            {"Jan 12, 2024", "Electric Bill", "Utilities", "-¥98.76"}
        };
        
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
        containerPanel.add(seasonalPanel);
        containerPanel.add(Box.createVerticalStrut(20));
        containerPanel.add(actionsPanel);
        
        // Add scroll pane for container panel
        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to main panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
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
        
        JLabel insightsText = new JLabel("<html>" +
                "<p>• Based on your spending patterns, you could save <b>¥350/month</b> by reducing dining out expenses.</p>" +
                "<p>• Your utility bills are <b>18% lower</b> than similar households in your region.</p>" +
                "<p>• <span style='color:#c0392b;'><b>Alert:</b> Chinese New Year preparations should start soon. Budget ¥800 based on last year.</span></p>" +
                "</html>");
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