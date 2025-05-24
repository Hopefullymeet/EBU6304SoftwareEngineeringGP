package view;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * AccountView - The account management screen.
 * This class displays account information and settings.
 */
public class AccountView extends JFrame {
    
    // UI Components
    private JPanel contentPanel;
    
    // Sidebar items
    private JPanel accountPanel;
    private JPanel billingPanel;
    private JPanel budgetPanel;
    private JPanel helpCenterPanel;
    private JPanel accountsManagerPanel; // New panel for managing multiple accounts
    private JPanel financialAdvisorPanel; // New panel for financial advisor
    
    // Account sub-items container
    private JPanel accountSubItems;
    private boolean accountExpanded = true;
    
    // Session timeout settings
    private JSpinner sessionTimeoutSpinner;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Color POSITIVE_GREEN = new Color(46, 204, 113);
    private final Color NEGATIVE_RED = new Color(231, 76, 60);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    // Content cards
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private final String PROFILE_PANEL = "ProfilePanel";
    private final String ACCOUNTS_MANAGER_PANEL = "AccountsManagerPanel";
    private final String PREFERENCES_PANEL = "PreferencesPanel";
    
    // Currency manager for handling currency display
    private CurrencyManager currencyManager;
    
    // Notification manager for handling notifications
    private NotificationManager notificationManager;
    
    // User current user
    private User currentUser;
    
    // Add static fields for CNY theme and budget boost at class level (after existing fields)
    // CNY Theme Settings
    public static boolean isCNYTheme = false;
    public static boolean isCNYBudgetBoost = false;
    public static final Color CNY_RED = new Color(220, 20, 60);
    public static final Color CNY_YELLOW = new Color(255, 215, 0);
    
    // 用户自定义预算（null表示未自定义）
    public static Double customBudget = null;
    public static void setCustomBudget(Double value) { customBudget = value; }
    public static Double getCustomBudget() { return customBudget; }
    public static void clearCustomBudget() { customBudget = null; }
    
    /**
     * Constructor for the AccountView
     */
    public AccountView() {
        // Initialize managers FIRST
        UserManager userManager = UserManager.getInstance();
        currencyManager = CurrencyManager.getInstance();
        notificationManager = NotificationManager.getInstance();
        
        // Get the current user BEFORE using it
        currentUser = userManager.getCurrentUser();
        
        // Check if currentUser is null and handle appropriately
        if (currentUser == null) {
            // Option 1: Set a default title and show error
            setTitle("Account Overview - Error");
            // It might be better to not even show the view if no user is logged in.
            // Consider closing the view and showing LoginView instead.
            // For now, we proceed but the view might be broken.
            System.err.println("AccountView Error: currentUser is null. No user logged in.");
             // Optionally show a message dialog
            // JOptionPane.showMessageDialog(null, "Error: No user logged in.", "Login Error", JOptionPane.ERROR_MESSAGE);
            // Optionally close this view
            // dispose();
            // return; // Prevent further initialization if desired
        } else {
            // Set title using the username only if currentUser is valid
            setTitle("Account Overview - " + currentUser.getUsername());
        }

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Only create UI components if currentUser is potentially valid (or handle null case within methods)
        if (currentUser != null) {
             createHeader();
             createSidebar();
             
             // Create card layout for different content panels
             cardLayout = new CardLayout();
             cardsPanel = new JPanel(cardLayout);
             cardsPanel.setBackground(Color.WHITE);
             
             // Add different content panels to the card layout
             createAccountContent();
             createAccountsManagerContent();
             createPreferencesContent();
             
             add(cardsPanel, BorderLayout.CENTER);
             
             // Default view is profile panel
             cardLayout.show(cardsPanel, PROFILE_PANEL);
             
             setLocationRelativeTo(null);
             setVisible(true);
        } else {
            // If currentUser is null, maybe just make the frame invisible or dispose it
             setVisible(false);
             dispose(); // Close the frame if no user
             // Potentially show LoginView again
             // new LoginView(); 
        }
    }
    
    /**
     * Creates the header panel with title and logout button
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setName("headerPanel");
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Account");
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
                // Log out the user
                UserManager.getInstance().logout();
                
                // Stop session monitoring
                SessionManager.getInstance().stopSession();
                
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
        accountPanel = createSidebarItem("Account", false, true);
        billingPanel = createSidebarItem("Billing & Subscriptions", false, false);
        budgetPanel = createSidebarItem("Budget", false, false);
        helpCenterPanel = createSidebarItem("Help Center", false, false);
        financialAdvisorPanel = createSidebarItem("Financial Advisor", false, false); // New financial advisor panel
        
        // Create Account sub-items container
        accountSubItems = new JPanel();
        accountSubItems.setLayout(new BoxLayout(accountSubItems, BoxLayout.Y_AXIS));
        accountSubItems.setBackground(Color.WHITE);
        accountSubItems.setVisible(accountExpanded);
        
        // Create Account sub-items
        JPanel profilePanel = createSidebarItem("Profile", true, true);
        JPanel accountsManagerPanel = createSidebarItem("Accounts Manager", true, false); // Moved to sub-item
        JPanel preferencesPanel = createSidebarItem("Preferences", true, false);
        
        // Add click listeners
        accountPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                toggleAccountSubItems();
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
        
        // Add click listener for Financial Advisor
        financialAdvisorPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                new FinancialAdvisorView();
            }
        });
        
        // Add click listeners for Account sub-items
        profilePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(profilePanel);
                cardLayout.show(cardsPanel, PROFILE_PANEL);
            }
        });
        
        // Add click listener for Accounts Manager (now as sub-item)
        accountsManagerPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(accountsManagerPanel);
                cardLayout.show(cardsPanel, ACCOUNTS_MANAGER_PANEL);
            }
        });
        
        preferencesPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                setActiveSidebarItem(preferencesPanel);
                // Show preferences settings
                cardLayout.show(cardsPanel, PREFERENCES_PANEL);
            }
        });
        
        // Add sub-items to container
        accountSubItems.add(profilePanel);
        accountSubItems.add(Box.createVerticalStrut(5));
        accountSubItems.add(accountsManagerPanel); // Add as sub-item here
        accountSubItems.add(Box.createVerticalStrut(5));
        accountSubItems.add(preferencesPanel);
        
        // Add sidebar items to panel
        sidebarPanel.add(accountPanel);
        sidebarPanel.add(accountSubItems);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(billingPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(budgetPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(helpCenterPanel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(financialAdvisorPanel); // Add financial advisor to sidebar
        
        // Add a glue component to push everything to the top
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Add sidebar to main frame
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * Toggle the visibility of Account sub-items
     */
    private void toggleAccountSubItems() {
        accountExpanded = !accountExpanded;
        accountSubItems.setVisible(accountExpanded);
        
        // Add arrow indicator to Account item
        JLabel accountLabel = (JLabel) ((JPanel) accountPanel.getComponent(0)).getComponent(0);
        accountLabel.setText("Account " + (accountExpanded ? "▼" : "▶"));
        
        // Refresh the UI
        accountPanel.revalidate();
        accountPanel.repaint();
    }
    
    /**
     * Sets the active sidebar item by changing its background color
     * @param activePanel The panel to set as active
     */
    private void setActiveSidebarItem(JPanel activePanel) {
        // Collect all sub-items
        Component[] subItems = accountSubItems.getComponents();
        
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
        if (text.equals("Account")) {
            displayText = text + " " + (accountExpanded ? "▼" : "▶");
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
     * Creates the account content panel
     */
    private void createAccountContent() {
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Account details panel
        JPanel accountDetails = new JPanel();
        accountDetails.setLayout(new BoxLayout(accountDetails, BoxLayout.Y_AXIS));
        accountDetails.setBackground(Color.WHITE);
        accountDetails.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Personal information section
        JLabel personalInfoLabel = new JLabel("Personal Information");
        personalInfoLabel.setFont(SUBHEADER_FONT);
        personalInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel personalInfoPanel = new JPanel();
        personalInfoPanel.setLayout(new GridLayout(3, 2, 15, 25));
        personalInfoPanel.setBackground(Color.WHITE);
        personalInfoPanel.setBorder(new EmptyBorder(20, 0, 40, 0));
        personalInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        personalInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Get current user information
        String username = currentUser != null ? currentUser.getUsername() : "John Doe";
        String email = currentUser != null ? currentUser.getEmail() : "john.doe@example.com";
        
        // Add personal information fields
        addFormField(personalInfoPanel, "Name:", username);
        addFormField(personalInfoPanel, "Email:", email);
        addFormField(personalInfoPanel, "Phone:", "(123) 456-7890");
        
        // Security settings section
        JLabel securityLabel = new JLabel("Security Settings");
        securityLabel.setFont(SUBHEADER_FONT);
        securityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel securityPanel = new JPanel();
        securityPanel.setLayout(new GridLayout(2, 2, 15, 25)); // Changed to 2 rows for session timeout
        securityPanel.setBackground(Color.WHITE);
        securityPanel.setBorder(new EmptyBorder(20, 0, 40, 0));
        securityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        securityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        
        // Add security fields
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(CONTENT_FONT);
        
        JPanel passwordButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordButtonPanel.setBackground(Color.WHITE);
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(CONTENT_FONT);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setPreferredSize(new Dimension(changePasswordButton.getPreferredSize().width, 40));
        changePasswordButton.setMargin(new Insets(8, 15, 8, 15));
        passwordButtonPanel.add(changePasswordButton);
        
        // Add action listener to Change Password button
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChangePasswordDialog();
            }
        });
        
        securityPanel.add(passwordLabel);
        securityPanel.add(passwordButtonPanel);
        
        // Add session timeout setting
        JLabel sessionTimeoutLabel = new JLabel("Session Timeout (minutes):");
        sessionTimeoutLabel.setFont(CONTENT_FONT);
        
        JPanel sessionTimeoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sessionTimeoutPanel.setBackground(Color.WHITE);
        
        // Get current timeout setting
        int currentTimeout = currentUser != null ? currentUser.getSessionTimeoutMinutes() : 1;
        
        // Create spinner for timeout selection
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(currentTimeout, 1, 60, 1);
        sessionTimeoutSpinner = new JSpinner(spinnerModel);
        sessionTimeoutSpinner.setFont(CONTENT_FONT);
        JComponent editor = sessionTimeoutSpinner.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        spinnerEditor.getTextField().setColumns(3);
        
        JLabel timeoutInfoLabel = new JLabel("  Auto-logout after inactivity");
        timeoutInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        timeoutInfoLabel.setForeground(DARK_GRAY);
        
        sessionTimeoutPanel.add(sessionTimeoutSpinner);
        sessionTimeoutPanel.add(timeoutInfoLabel);
        
        securityPanel.add(sessionTimeoutLabel);
        securityPanel.add(sessionTimeoutPanel);
        
        // Add components to account details panel
        accountDetails.add(personalInfoLabel);
        accountDetails.add(personalInfoPanel);
        accountDetails.add(securityLabel);
        accountDetails.add(securityPanel);
        
        // Add buttons for account actions
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(PRIMARY_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(CONTENT_FONT);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(saveButton.getPreferredSize().width, 45));
        saveButton.setMargin(new Insets(10, 20, 10, 20));
        
        // Add action listener for save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAccountSettings();
            }
        });
        
        actionsPanel.add(saveButton);
        
        // Add scroll pane for account details
        JScrollPane scrollPane = new JScrollPane(accountDetails);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add components to main panel
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        // Add to cards panel
        cardsPanel.add(contentPanel, PROFILE_PANEL);
    }
    
    /**
     * Adds a form field with label and value to a panel
     * @param panel The panel to add the field to
     * @param labelText The label text
     * @param valueText The value text
     */
    private void addFormField(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(CONTENT_FONT);
        
        JTextField textField = new JTextField(valueText);
        textField.setFont(CONTENT_FONT);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 40));
        textField.setMargin(new Insets(8, 10, 8, 10));
        
        panel.add(label);
        panel.add(textField);
    }
    
    /**
     * Saves account settings including session timeout
     */
    private void saveAccountSettings() {
        // Update session timeout
        int newTimeout = (Integer) sessionTimeoutSpinner.getValue();
        
        UserManager userManager = UserManager.getInstance();
        
        if (userManager.updateSessionTimeout(newTimeout)) {
            // Also update the current session timer with the new timeout
            SessionManager.getInstance().stopSession();
            SessionManager.getInstance().startSession(this);
            
            JOptionPane.showMessageDialog(
                this,
                "Settings saved successfully.",
                "Settings Saved",
                JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Failed to save settings.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Creates the accounts manager content panel for managing multiple accounts
     */
    private void createAccountsManagerContent() {
        JPanel accountsManagerContent = new JPanel();
        accountsManagerContent.setBackground(Color.WHITE);
        accountsManagerContent.setLayout(new BorderLayout());
        accountsManagerContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel accountsContainer = new JPanel();
        accountsContainer.setLayout(new BoxLayout(accountsContainer, BoxLayout.Y_AXIS));
        accountsContainer.setBackground(Color.WHITE);
        
        // 预算概览面板 - 修改标签为预算相关内容
        JPanel budgetSummaryPanel = new JPanel(new BorderLayout());
        budgetSummaryPanel.setBackground(LIGHT_GRAY);
        budgetSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        budgetSummaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        budgetSummaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel remainingBudgetLabel = new JLabel("Remaining Budget");
        remainingBudgetLabel.setFont(SUBHEADER_FONT);
        
        // 获取当前用户的预算信息
        double totalBudget = AccountView.customBudget != null ? 
            AccountView.customBudget : (AccountView.isCNYBudgetBoost ? 10000.00 : 5000.00);
        
        // 加载所有交易记录一次，避免重复加载 - 使用loadAllTransactions来获取所有用户的交易
        List<Transaction> allTransactions = TransactionManager.loadAllTransactions();
        
        // 获取消费总额 - 从 TransactionManager 中获取
        double totalSpent = 0.0;
        for (Transaction transaction : allTransactions) {
            // 只计算当前用户的消费总额
            if (currentUser != null && transaction.getUserId().equals(currentUser.getUsername())) {
                totalSpent += transaction.getAmount();
            }
        }
        
        // 计算剩余预算
        double remainingBudget = totalBudget - totalSpent;
        
        // 获取当前用户的货币偏好
        String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
        
        // 格式化金额
        String formattedRemainingBudget = currencyManager.format(remainingBudget, userCurrency);
        String formattedTotalBudget = currencyManager.format(totalBudget, userCurrency);
        String formattedTotalSpent = currencyManager.format(totalSpent, userCurrency);
        
        // 更新标签
        JLabel remainingBudgetValue = new JLabel(formattedRemainingBudget);
        remainingBudgetValue.setFont(new Font("Arial", Font.BOLD, 24));
        remainingBudgetValue.setForeground(POSITIVE_GREEN);
        
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        detailsPanel.setBackground(LIGHT_GRAY);
        
        // 添加详细信息 - 总预算、消费、剩余预算
        addAssetDetail(detailsPanel, "Total Budget", formattedTotalBudget, POSITIVE_GREEN);
        addAssetDetail(detailsPanel, "Spent", formattedTotalSpent, NEGATIVE_RED);
        addAssetDetail(detailsPanel, "Remaining", formattedRemainingBudget, POSITIVE_GREEN);
        
        budgetSummaryPanel.add(remainingBudgetLabel, BorderLayout.NORTH);
        budgetSummaryPanel.add(remainingBudgetValue, BorderLayout.WEST);
        budgetSummaryPanel.add(detailsPanel, BorderLayout.EAST);
        
        // 用户账户部分标题
        JLabel accountsLabel = new JLabel("User Accounts");
        accountsLabel.setFont(SUBHEADER_FONT);
        accountsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountsLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // 创建用户账户列表
        JPanel accountsList = new JPanel();
        accountsList.setLayout(new BoxLayout(accountsList, BoxLayout.Y_AXIS));
        accountsList.setBackground(Color.WHITE);
        accountsList.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // 从 UserManager 中获取所有用户
        Map<String, User> allUsers = UserManager.getInstance().getAllUsers();
        
        if (allUsers != null && !allUsers.isEmpty()) {
            // 遍历所有用户，添加到列表
            for (User user : allUsers.values()) {
                // 为每个用户获取其预算信息
                double userBudget = 5000.0; // 默认预算
                double userSpent = 0.0;
                double userRemaining = userBudget;
                
                // 给每个用户单独计算预算和消费 - 使用真实交易数据
                // 获取用户的自定义预算设置，或者使用默认值
                userBudget = AccountView.customBudget != null ? 
                    AccountView.customBudget : (AccountView.isCNYBudgetBoost ? 10000.00 : 5000.00);
                    
                // 计算用户的消费总额 - 不需要创建新的交易列表，直接累加
                userSpent = 0.0; // 重置消费额
                for (Transaction transaction : allTransactions) {
                    // 如果交易记录属于当前遍历的用户，则计入该用户的消费总额
                    if (transaction.getUserId() != null && transaction.getUserId().equals(user.getUsername())) {
                        userSpent += transaction.getAmount();
                    }
                }
                
                // 如果没有消费记录，使用默认/示例值
                if (userSpent == 0.0) {
                    // 为每个用户模拟不同的固定消费额，而不是随机值
                    // 基于用户名的哈希来生成稳定的消费额 (0-3000范围)
                    int hash = Math.abs(user.getUsername().hashCode());
                    userSpent = 500 + (hash % 25) * 100; // 500-3000范围内，以100为步长
                }
                
                // 计算剩余预算
                userRemaining = userBudget - userSpent;
                
                // 格式化剩余预算金额
                String formattedUserRemaining = currencyManager.format(userRemaining, userCurrency);
                
                // 添加用户账户卡片 - 显示剩余预算
                addUserAccountCard(accountsList, user.getUsername(), user.getEmail(), formattedUserRemaining);
            }
        } else {
            // 如果没有用户，显示提示
            JLabel noUsersLabel = new JLabel("No user accounts found");
            noUsersLabel.setFont(CONTENT_FONT);
            noUsersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            accountsList.add(noUsersLabel);
        }
        
        // 添加新用户按钮
        JButton addAccountButton = new JButton("+ Register New User");
        addAccountButton.setFont(CONTENT_FONT);
        addAccountButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addAccountButton.setBackground(PRIMARY_BLUE);
        addAccountButton.setForeground(Color.WHITE);
        addAccountButton.setOpaque(true);
        addAccountButton.setBorderPainted(false);
        addAccountButton.setFocusPainted(false);
        addAccountButton.setMaximumSize(new Dimension(200, 40));
        addAccountButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        addAccountButton.setMargin(new Insets(10, 15, 10, 15));
        addAccountButton.setActionCommand("addAccount");
        
        // 添加注册新用户按钮的动作
        addAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭当前窗口
                new RegisterView(); // 打开注册页面
            }
        });
        
        // 添加组件到账户容器
        accountsContainer.add(budgetSummaryPanel);
        accountsContainer.add(accountsLabel);
        accountsContainer.add(accountsList);
        accountsContainer.add(Box.createVerticalStrut(20));
        accountsContainer.add(addAccountButton);
        
        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(accountsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        accountsManagerContent.add(scrollPane, BorderLayout.CENTER);
        
        // 添加到卡片面板
        cardsPanel.add(accountsManagerContent, ACCOUNTS_MANAGER_PANEL);
    }
    
    /**
     * 添加一个用户账户卡片
     * @param panel 要添加卡片的面板
     * @param username 用户名
     * @param email 邮箱
     * @param budget 预算金额
     */
    private void addUserAccountCard(JPanel panel, String username, String email, String budget) {
        JPanel accountCard = new JPanel(new BorderLayout());
        accountCard.setBackground(Color.WHITE);
        accountCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        accountCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        accountCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // 用户头像占位符
        JPanel iconPlaceholder = new JPanel();
        iconPlaceholder.setBackground(LIGHT_GRAY);
        iconPlaceholder.setPreferredSize(new Dimension(50, 50));
        
        // 在圆形区域中显示用户名首字母
        JLabel initialLabel = new JLabel(username.substring(0, 1).toUpperCase());
        initialLabel.setFont(new Font("Arial", Font.BOLD, 20));
        initialLabel.setForeground(Color.WHITE);
        initialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        iconPlaceholder.setLayout(new BorderLayout());
        iconPlaceholder.add(initialLabel, BorderLayout.CENTER);
        
        // 用户信息面板
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(CONTENT_FONT);
        emailLabel.setForeground(DARK_GRAY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createVerticalStrut(3));
        userInfoPanel.add(emailLabel);
        
        // 预算标签
        JLabel budgetLabel = new JLabel(budget);
        budgetLabel.setFont(new Font("Arial", Font.BOLD, 16));
        budgetLabel.setForeground(POSITIVE_GREEN);
        
        // 切换账户按钮
        JButton switchButton = new JButton("Switch Account");
        switchButton.setFont(new Font("Arial", Font.PLAIN, 12));
        switchButton.setFocusPainted(false);
        switchButton.setBorderPainted(false);
        switchButton.setBackground(PRIMARY_BLUE);
        switchButton.setForeground(Color.WHITE);
        
        // 判断是否为当前用户
        boolean isCurrentUser = currentUser != null && username.equals(currentUser.getUsername());
        if (isCurrentUser) {
            switchButton.setText("Current Account");
            switchButton.setEnabled(false);
        }
        
        // 添加切换账户按钮的动作
        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isCurrentUser) {
                    switchToUser(username);
                }
            }
        });
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(budgetLabel, BorderLayout.NORTH);
        rightPanel.add(switchButton, BorderLayout.SOUTH);
        
        accountCard.add(iconPlaceholder, BorderLayout.WEST);
        accountCard.add(userInfoPanel, BorderLayout.CENTER);
        accountCard.add(rightPanel, BorderLayout.EAST);
        
        // 添加卡片到面板
        panel.add(accountCard);
        panel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * 切换到指定用户
     * @param username 要切换到的用户名
     */
    private void switchToUser(String username) {
        // 创建一个对话框让用户输入密码
        JDialog loginDialog = new JDialog(this, "Account Switch", true);
        loginDialog.setSize(350, 200);
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(username);
        usernameField.setEditable(false); // 用户名不可编辑
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> loginDialog.dispose());
        
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true); // 确保按钮背景不透明
        loginButton.setBorderPainted(false);
        
        // 登录按钮动作
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passwordField.getPassword());
                
                // 验证密码
                if (UserManager.getInstance().authenticateUser(username, password)) {
                    // 登录成功，关闭对话框
                    loginDialog.dispose();
                    
                    // 停止当前会话监控
                    SessionManager.getInstance().stopSession();
                    
                    // 关闭当前视图并打开新的账户视图
                    dispose();
                    new AccountView();
                } else {
                    // 密码错误
                    JOptionPane.showMessageDialog(
                        loginDialog,
                        "Incorrect password. Please try again.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                    passwordField.setText(""); // 清空密码字段
                }
            }
        });
        
        // Apply theme to buttons if CNY theme is active
        if (AccountView.isCNYTheme) {
            cancelButton.setBackground(AccountView.CNY_YELLOW);
            cancelButton.setForeground(AccountView.CNY_RED);
            cancelButton.setOpaque(true);
            cancelButton.setBorderPainted(false);
            
            loginButton.setBackground(AccountView.CNY_YELLOW);
            loginButton.setForeground(AccountView.CNY_RED);
        }
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);
        
        loginDialog.add(inputPanel, BorderLayout.CENTER);
        loginDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置回车键触发登录按钮
        loginDialog.getRootPane().setDefaultButton(loginButton);
        
        // 显示对话框
        loginDialog.setVisible(true);
    }
    
    /**
     * Adds an asset detail to the specified panel
     * @param panel The panel to add the detail to
     * @param label The label text
     * @param value The value text
     * @param valueColor The color for the value
     */
    private void addAssetDetail(JPanel panel, String label, String value, Color valueColor) {
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBackground(LIGHT_GRAY);
        
        JLabel detailLabel = new JLabel(label);
        detailLabel.setFont(CONTENT_FONT);
        detailLabel.setForeground(DARK_GRAY);
        detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel detailValue = new JLabel(value);
        detailValue.setFont(new Font("Arial", Font.BOLD, 16));
        detailValue.setForeground(valueColor);
        detailValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        detailPanel.add(detailLabel);
        detailPanel.add(Box.createVerticalStrut(5));
        detailPanel.add(detailValue);
        
        panel.add(detailPanel);
    }
    
    /**
     * Creates the preferences content panel
     */
    private void createPreferencesContent() {
        JPanel preferencesPanel = new JPanel();
        preferencesPanel.setBackground(Color.WHITE);
        preferencesPanel.setLayout(new BorderLayout());
        preferencesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Get current user and preferences
        User currentUser = UserManager.getInstance().getCurrentUser();
        
        // Create main content panel with vertical box layout
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        
        // Preferences header
        JLabel preferencesHeaderLabel = new JLabel("Preferences");
        preferencesHeaderLabel.setFont(SUBHEADER_FONT);
        preferencesHeaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        preferencesHeaderLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // 1. Currency settings
        JPanel currencyPanel = new JPanel();
        currencyPanel.setLayout(new BoxLayout(currencyPanel, BoxLayout.Y_AXIS));
        currencyPanel.setBackground(Color.WHITE);
        currencyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel currencyLabel = new JLabel("Display Currency");
        currencyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currencyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel currencyOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        currencyOptionsPanel.setBackground(Color.WHITE);
        currencyOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] currencies = {CurrencyManager.CNY, CurrencyManager.USD, CurrencyManager.EUR};
        JComboBox<String> currencyCombo = new JComboBox<>(currencies);
        currencyCombo.setSelectedItem(currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY);
        currencyCombo.setFont(CONTENT_FONT);
        currencyCombo.setPreferredSize(new Dimension(200, 30));
        
        // Add action listener to immediately show currency changes in preview
        currencyCombo.addActionListener(e -> {
                String newCurrency = (String) currencyCombo.getSelectedItem();
                // Preview panel to show example values in the selected currency
                updateCurrencyPreview(newCurrency, currencyPanel);
        });
        
        currencyOptionsPanel.add(currencyCombo);
        
        // Add a preview panel to show currency conversion example
        JPanel currencyPreviewPanel = new JPanel();
        currencyPreviewPanel.setLayout(new BoxLayout(currencyPreviewPanel, BoxLayout.Y_AXIS));
        currencyPreviewPanel.setBackground(new Color(245, 245, 245));
        currencyPreviewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        currencyPreviewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyPreviewPanel.setMaximumSize(new Dimension(400, 100));
        
        // Initial preview content
        updateCurrencyPreview((String) currencyCombo.getSelectedItem(), currencyPanel);
        
        currencyPanel.add(currencyLabel);
        currencyPanel.add(Box.createVerticalStrut(10));
        currencyPanel.add(currencyOptionsPanel);
        currencyPanel.add(Box.createVerticalStrut(10));
        currencyPanel.add(currencyPreviewPanel); // Add the preview panel
        
        // 2. Chinese New Year Personalization Section
        JPanel cnyPanel = new JPanel();
        cnyPanel.setLayout(new BoxLayout(cnyPanel, BoxLayout.Y_AXIS));
        cnyPanel.setBackground(Color.WHITE);
        cnyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cnyPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel cnyLabel = new JLabel("Chinese New Year Personalization");
        cnyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cnyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Theme switch
        JCheckBox themeSwitch = new JCheckBox("Red & Yellow Theme (All Pages)");
        themeSwitch.setFont(CONTENT_FONT);
        themeSwitch.setBackground(Color.WHITE);
        themeSwitch.setSelected(isCNYTheme);
        
        // Budget boost switch
        JCheckBox budgetSwitch = new JCheckBox("Boost Budget to 10,000 (Budget Page)");
        budgetSwitch.setFont(CONTENT_FONT);
        budgetSwitch.setBackground(Color.WHITE);
        budgetSwitch.setSelected(isCNYBudgetBoost);
        
        // Add listeners
        themeSwitch.addActionListener(e -> {
            isCNYTheme = themeSwitch.isSelected();
            applyThemeToAllComponents(this);
            JOptionPane.showMessageDialog(this, 
                "Theme settings will be applied when you navigate between pages.", 
                "Theme Updated", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        budgetSwitch.addActionListener(e -> {
            isCNYBudgetBoost = budgetSwitch.isSelected();
            JOptionPane.showMessageDialog(this, 
                "Budget boost setting will take effect when you visit the Budget page.", 
                "Budget Updated", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        cnyPanel.add(cnyLabel);
        cnyPanel.add(Box.createVerticalStrut(10));
        cnyPanel.add(themeSwitch);
        cnyPanel.add(Box.createVerticalStrut(5));
        cnyPanel.add(budgetSwitch);
        
        // Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton savePreferencesButton = new JButton("Save Preferences");
        savePreferencesButton.setBackground(PRIMARY_BLUE);
        savePreferencesButton.setForeground(Color.WHITE);
        savePreferencesButton.setFont(CONTENT_FONT);
        savePreferencesButton.setFocusPainted(false);
        savePreferencesButton.setOpaque(true);
        savePreferencesButton.setBorderPainted(false);
        
        // Add action listener for the save button
        savePreferencesButton.addActionListener(e -> {
            if (currentUser != null) {
                // Store the original currency for comparison
                String originalCurrency = currentUser.getCurrency();
                
                // Save currency preference
                String newCurrency = (String) currencyCombo.getSelectedItem();
                currentUser.setCurrency(newCurrency);
                
                // Update the user in the UserManager
                boolean saved = UserManager.getInstance().updateCurrentUser(currentUser);
                
                if (saved) {
                    JOptionPane.showMessageDialog(this, 
                        "Preferences saved successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    // If currency changed, update the display immediately
                    if (!originalCurrency.equals(newCurrency)) {
                        updateCurrencyDisplay(newCurrency);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save preferences. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cannot save preferences: No user is currently logged in.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(savePreferencesButton);
        
        // Add components to main content panel
        mainContent.add(preferencesHeaderLabel);
        mainContent.add(currencyPanel);
        mainContent.add(cnyPanel);
        mainContent.add(buttonPanel);
        
        // Create a scroll pane for the main content
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        preferencesPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add to cards panel
        cardsPanel.add(preferencesPanel, PREFERENCES_PANEL);
    }
    
    /**
     * Updates the currency preview panel with sample values in the selected currency
     * @param currency The selected currency
     * @param container The container that holds the preview panel
     */
    private void updateCurrencyPreview(String currency, JPanel container) {
        // Find and remove the existing preview panel if it exists
        Component[] components = container.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp.getMaximumSize().height == 100) {
                container.remove(comp);
                break;
            }
        }
        
        // Create a new preview panel
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));
        previewPanel.setBackground(new Color(245, 245, 245));
        previewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        previewPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        previewPanel.setMaximumSize(new Dimension(400, 100));
        
        // Example amount in CNY
        double sampleAmount = 1000.00; // 1000 CNY
        
        // Convert to selected currency
        double convertedAmount = currencyManager.convert(sampleAmount, CurrencyManager.CNY, currency);
        
        // Format the amount
        String formattedAmount = currencyManager.format(convertedAmount, currency);
        
        // Add preview information
        JLabel previewLabel = new JLabel("Example: 1,000.00 CNY equals " + formattedAmount);
        previewLabel.setFont(CONTENT_FONT);
        previewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("All amounts will be displayed in your selected currency");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(DARK_GRAY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        previewPanel.add(previewLabel);
        previewPanel.add(Box.createVerticalStrut(5));
        previewPanel.add(infoLabel);
        
        // Add the preview panel
        container.add(previewPanel);
        container.revalidate();
        container.repaint();
    }
    
    /**
     * Updates currency display across the UI when currency preference changes
     * @param newCurrency The new currency setting
     */
    private void updateCurrencyDisplay(String newCurrency) {
        // This method will be called when the user changes currency in preferences
        // Refresh the accounts manager view to show amounts in new currency
        createAccountsManagerContent();
    }
    
    /**
     * Shows a dialog for changing the user's password
     */
    private void showChangePasswordDialog() {
        // Create the dialog
        JDialog changePasswordDialog = new JDialog(this, "Change Password", true);
        changePasswordDialog.setSize(400, 300);
        changePasswordDialog.setLocationRelativeTo(this);
        changePasswordDialog.setLayout(new BorderLayout());
        
        // Create the input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 20));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Current password field
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(CONTENT_FONT);
        JPasswordField currentPasswordField = new JPasswordField();
        
        // New password field
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(CONTENT_FONT);
        JPasswordField newPasswordField = new JPasswordField();
        
        // Confirm new password field
        JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");
        confirmPasswordLabel.setFont(CONTENT_FONT);
        JPasswordField confirmPasswordField = new JPasswordField();
        
        // Add fields to the input panel
        inputPanel.add(currentPasswordLabel);
        inputPanel.add(currentPasswordField);
        inputPanel.add(newPasswordLabel);
        inputPanel.add(newPasswordField);
        inputPanel.add(confirmPasswordLabel);
        inputPanel.add(confirmPasswordField);
        
        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(CONTENT_FONT);
        cancelButton.addActionListener(e -> changePasswordDialog.dispose());
        
        // Change password button
        JButton changeButton = new JButton("Change Password");
        changeButton.setFont(CONTENT_FONT);
        changeButton.setBackground(PRIMARY_BLUE);
        changeButton.setForeground(Color.WHITE);
        changeButton.setOpaque(true);
        changeButton.setBorderPainted(false);
        changeButton.setFocusPainted(false);
        
        // Add action listener for the change password button
        changeButton.addActionListener(e -> {
            // Get the password values
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Validate inputs
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(changePasswordDialog, 
                    "All fields are required", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(changePasswordDialog, 
                    "New password and confirm password do not match", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate minimum password length
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(changePasswordDialog, 
                    "New password must be at least 6 characters long", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Attempt to change the password
            UserManager userManager = UserManager.getInstance();
            boolean success = userManager.changePassword(currentPassword, newPassword);
            
            if (success) {
                JOptionPane.showMessageDialog(changePasswordDialog, 
                    "Password changed successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                changePasswordDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(changePasswordDialog, 
                    "Failed to change password. Please check your current password and try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add buttons to the button panel
        buttonPanel.add(cancelButton);
        buttonPanel.add(changeButton);
        
        // Add panels to the dialog
        changePasswordDialog.add(inputPanel, BorderLayout.CENTER);
        changePasswordDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show the dialog
        changePasswordDialog.setVisible(true);
    }
    
    /**
     * Applies CNY theme to all components in the frame
     * @param frame The frame containing components to update
     */
    public static void applyThemeToAllComponents(JFrame frame) {
        applyThemeToComponent(frame.getContentPane());
        frame.repaint();
    }
    
    /**
     * Recursively applies CNY theme to components
     * @param component The component to update
     */
    public static void applyThemeToComponent(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            
            // 特殊处理顶部面板
            if (panel.getName() != null && panel.getName().equals("headerPanel")) {
                panel.setBackground(isCNYTheme ? CNY_RED : new Color(52, 152, 219)); // PRIMARY_BLUE
            } else {
                panel.setBackground(isCNYTheme ? CNY_RED : Color.WHITE);
            }
            
            for (Component child : panel.getComponents()) {
                applyThemeToComponent(child);
            }
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            if (isCNYTheme) {
                button.setBackground(CNY_YELLOW);
                button.setForeground(CNY_RED);
            } else {
                // 根据按钮文本区分不同按钮
                if (button.getText().equals("Logout")) {
                    button.setBackground(new Color(231, 76, 60)); // 红色
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(new Color(52, 152, 219)); // PRIMARY_BLUE
                    button.setForeground(Color.WHITE);
                }
            }
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            
            // 特殊处理LoginView的标题
            if (label.getText() != null && label.getText().equals("Finance Tracker")) {
                label.setForeground(isCNYTheme ? CNY_YELLOW : new Color(52, 152, 219)); // PRIMARY_BLUE
            }
            // 根据字体大小或样式来区分不同标签
            else if (label.getFont().getSize() >= 20) { // 大标题或头部标签
                label.setForeground(isCNYTheme ? CNY_YELLOW : Color.WHITE);
            } else if (label.getFont().getStyle() == Font.BOLD) { // 粗体标签
                label.setForeground(isCNYTheme ? CNY_YELLOW : new Color(100, 100, 100)); // DARK_GRAY
            } else {
                label.setForeground(isCNYTheme ? CNY_YELLOW : Color.BLACK);
            }
        }
    }
    
    /**
     * Main method to test the AccountView
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
                new AccountView();
            }
        });
    }
} 