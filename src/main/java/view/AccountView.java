package view;

import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        
        // Header with total assets summary
        JPanel assetSummaryPanel = new JPanel(new BorderLayout());
        assetSummaryPanel.setBackground(LIGHT_GRAY);
        assetSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        assetSummaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        assetSummaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel totalAssetsLabel = new JLabel("Total Assets");
        totalAssetsLabel.setFont(SUBHEADER_FONT);
        
        // Get current user's currency preference
        String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
        
        // Convert total assets to user's currency and format it
        double totalAssetsInCNY = 12458.92; // Base amount in CNY
        double totalAssetsInUserCurrency = currencyManager.convert(totalAssetsInCNY, CurrencyManager.CNY, userCurrency);
        String formattedTotalAssets = currencyManager.format(totalAssetsInUserCurrency, userCurrency);
        
        // Format other amounts
        double assetsInCNY = 15250.75;
        double debtsInCNY = -2791.83;
        double netWorthInCNY = 12458.92;
        
        // Convert to user currency
        double assetsInUserCurrency = currencyManager.convert(assetsInCNY, CurrencyManager.CNY, userCurrency);
        double debtsInUserCurrency = currencyManager.convert(debtsInCNY, CurrencyManager.CNY, userCurrency);
        double netWorthInUserCurrency = currencyManager.convert(netWorthInCNY, CurrencyManager.CNY, userCurrency);
        
        // Format amounts
        String formattedAssets = currencyManager.format(assetsInUserCurrency, userCurrency);
        String formattedDebts = currencyManager.format(debtsInUserCurrency, userCurrency);
        String formattedNetWorth = currencyManager.format(netWorthInUserCurrency, userCurrency);
        
        // Update labels
        JLabel totalAssetsValue = new JLabel(formattedTotalAssets);
        totalAssetsValue.setFont(new Font("Arial", Font.BOLD, 24));
        totalAssetsValue.setForeground(POSITIVE_GREEN);
        
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        detailsPanel.setBackground(LIGHT_GRAY);
        
        addAssetDetail(detailsPanel, "Assets", formattedAssets, POSITIVE_GREEN);
        addAssetDetail(detailsPanel, "Debts", formattedDebts, NEGATIVE_RED);
        addAssetDetail(detailsPanel, "Net Worth", formattedNetWorth, POSITIVE_GREEN);
        
        assetSummaryPanel.add(totalAssetsLabel, BorderLayout.NORTH);
        assetSummaryPanel.add(totalAssetsValue, BorderLayout.WEST);
        assetSummaryPanel.add(detailsPanel, BorderLayout.EAST);
        
        // Section title
        JLabel accountsLabel = new JLabel("My Accounts");
        accountsLabel.setFont(SUBHEADER_FONT);
        accountsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountsLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // Create account cards
        JPanel accountsList = new JPanel();
        accountsList.setLayout(new BoxLayout(accountsList, BoxLayout.Y_AXIS));
        accountsList.setBackground(Color.WHITE);
        accountsList.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Banking accounts
        JLabel bankAccountsLabel = new JLabel("Banking");
        bankAccountsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bankAccountsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bankAccountsLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        
        // Add Chinese bank accounts
        addAccountCard(accountsList, "Bank of China", "Checking", "¥2,453.65", "account-bank.png");
        addAccountCard(accountsList, "ICBC", "Savings", "¥8,257.12", "account-bank.png");
        addAccountCard(accountsList, "China Construction Bank", "Checking", "¥1,785.30", "account-bank.png");
        addAccountCard(accountsList, "Agricultural Bank of China", "Savings", "¥925.45", "account-bank.png");
        
        // Credit accounts
        JLabel creditAccountsLabel = new JLabel("Credit Cards");
        creditAccountsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        creditAccountsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditAccountsLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add Chinese credit cards
        addAccountCard(accountsList, "China Merchants Bank", "Credit Card", "-¥1,853.27", "account-credit.png");
        addAccountCard(accountsList, "Bank of Communications", "Credit Card", "-¥938.56", "account-credit.png");
        
        // Digital wallet accounts
        JLabel digitalWalletLabel = new JLabel("Digital Wallets");
        digitalWalletLabel.setFont(new Font("Arial", Font.BOLD, 16));
        digitalWalletLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        digitalWalletLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add Chinese digital wallets
        addAccountCard(accountsList, "Alipay", "Digital Wallet", "¥345.98", "account-digital.png");
        addAccountCard(accountsList, "WeChat Pay", "Digital Wallet", "¥154.23", "account-digital.png");
        
        // Investments
        JLabel investmentsLabel = new JLabel("Investments");
        investmentsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        investmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        investmentsLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add investment accounts
        addAccountCard(accountsList, "E-Fund", "Fund Investment", "¥3,452.87", "account-investment.png");
        addAccountCard(accountsList, "China Securities", "Stock Account", "¥586.90", "account-investment.png");
        
        // Add account button with dialog for bank selection
        JButton addAccountButton = new JButton("+ Add New Account");
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
        
        // Add action listener for the add account button
        addAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddAccountDialog();
            }
        });
        
        // Add components to the accounts container
        accountsContainer.add(assetSummaryPanel);
        accountsContainer.add(accountsLabel);
        accountsContainer.add(bankAccountsLabel);
        accountsContainer.add(accountsList);
        accountsContainer.add(Box.createVerticalStrut(20));
        accountsContainer.add(addAccountButton);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(accountsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        accountsManagerContent.add(scrollPane, BorderLayout.CENTER);
        
        // Add to cards panel
        cardsPanel.add(accountsManagerContent, ACCOUNTS_MANAGER_PANEL);
    }
    
    /**
     * Shows a dialog for adding a new account with bank selection
     */
    private void showAddAccountDialog() {
        JDialog addAccountDialog = new JDialog(this, "Add New Account", true);
        addAccountDialog.setSize(400, 350);
        addAccountDialog.setLocationRelativeTo(this);
        addAccountDialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 20));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Bank selection
        JLabel bankLabel = new JLabel("Bank:");
        bankLabel.setFont(CONTENT_FONT);
        
        String[] bankOptions = {
            "Select your bank",
            "Bank of China", 
            "ICBC", 
            "China Construction Bank", 
            "Agricultural Bank of China", 
            "Bank of Communications", 
            "China Merchants Bank", 
            "Other"
        };
        
        JComboBox<String> bankComboBox = new JComboBox<>(bankOptions);
        bankComboBox.setSelectedIndex(0);
        
        // Account type
        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setFont(CONTENT_FONT);
        
        String[] accountTypeOptions = {
            "Select account type",
            "Checking", 
            "Savings", 
            "Credit Card", 
            "Digital Wallet", 
            "Investment"
        };
        
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypeOptions);
        accountTypeComboBox.setSelectedIndex(0);
        
        // Account name
        JLabel accountNameLabel = new JLabel("Account Name:");
        accountNameLabel.setFont(CONTENT_FONT);
        
        JTextField accountNameField = new JTextField();
        
        // Initial balance
        JLabel balanceLabel = new JLabel("Initial Balance:");
        balanceLabel.setFont(CONTENT_FONT);
        
        JTextField balanceField = new JTextField();
        
        // Add fields to form
        formPanel.add(bankLabel);
        formPanel.add(bankComboBox);
        formPanel.add(accountTypeLabel);
        formPanel.add(accountTypeComboBox);
        formPanel.add(accountNameLabel);
        formPanel.add(accountNameField);
        formPanel.add(balanceLabel);
        formPanel.add(balanceField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(CONTENT_FONT);
        cancelButton.addActionListener(e -> addAccountDialog.dispose());
        
        JButton addButton = new JButton("Add Account");
        addButton.setFont(CONTENT_FONT);
        addButton.setBackground(PRIMARY_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        
        addButton.addActionListener(e -> {
            // Simple validation
            if (bankComboBox.getSelectedIndex() == 0 || 
                accountTypeComboBox.getSelectedIndex() == 0 ||
                accountNameField.getText().trim().isEmpty() ||
                balanceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(addAccountDialog, 
                    "Please fill in all fields", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Here you would normally save the account to a database
            // For now, just close the dialog
            JOptionPane.showMessageDialog(addAccountDialog, 
                "Account added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            addAccountDialog.dispose();
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(addButton);
        
        // Add panels to dialog
        addAccountDialog.add(formPanel, BorderLayout.CENTER);
        addAccountDialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        addAccountDialog.setVisible(true);
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
     * Adds an account card to the accounts list
     * @param panel The panel to add the account card to
     * @param accountName The name of the account
     * @param accountType The type of account
     * @param balance The account balance
     * @param iconPath The path to the account icon
     */
    private void addAccountCard(JPanel panel, String accountName, String accountType, String balance, String iconPath) {
        JPanel accountCard = new JPanel(new BorderLayout());
        accountCard.setBackground(Color.WHITE);
        accountCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));
        accountCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        accountCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // For demo purposes, we'll create a placeholder for the icon
        JPanel iconPlaceholder = new JPanel();
        iconPlaceholder.setBackground(LIGHT_GRAY);
        iconPlaceholder.setPreferredSize(new Dimension(50, 50));
        
        JPanel accountInfoPanel = new JPanel();
        accountInfoPanel.setLayout(new BoxLayout(accountInfoPanel, BoxLayout.Y_AXIS));
        accountInfoPanel.setBackground(Color.WHITE);
        accountInfoPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel nameLabel = new JLabel(accountName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(accountType);
        typeLabel.setFont(CONTENT_FONT);
        typeLabel.setForeground(DARK_GRAY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        accountInfoPanel.add(nameLabel);
        accountInfoPanel.add(Box.createVerticalStrut(3));
        accountInfoPanel.add(typeLabel);
        
        JLabel balanceLabel = new JLabel(balance);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(balance.startsWith("-") ? NEGATIVE_RED : POSITIVE_GREEN);
        
        // Button to manage account
        JButton manageButton = new JButton("Manage");
        manageButton.setFont(new Font("Arial", Font.PLAIN, 12));
        manageButton.setFocusPainted(false);
        manageButton.setBorderPainted(false);
        manageButton.setBackground(PRIMARY_BLUE);
        manageButton.setForeground(Color.WHITE);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(balanceLabel, BorderLayout.NORTH);
        rightPanel.add(manageButton, BorderLayout.SOUTH);
        
        accountCard.add(iconPlaceholder, BorderLayout.WEST);
        accountCard.add(accountInfoPanel, BorderLayout.CENTER);
        accountCard.add(rightPanel, BorderLayout.EAST);
        
        // Convert and format balance
        try {
            String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
            
            // Parse the original balance from CNY string
            String cnyValueStr = balance.replace("¥", "").replace(",", "").trim();
            double cnyValue = Double.parseDouble(cnyValueStr);
            
            // Convert to user currency
            double userCurrencyValue = currencyManager.convert(cnyValue, CurrencyManager.CNY, userCurrency);
            
            // Format balance
            String formattedBalance = currencyManager.format(userCurrencyValue, userCurrency);
            balanceLabel.setText(formattedBalance);
        } catch (NumberFormatException e) {
            // If parsing fails, use the original balance
            balanceLabel.setText(balance);
        }
        
        // Add the account card to the panel with some spacing
        panel.add(accountCard);
        panel.add(Box.createVerticalStrut(10));
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
        currencyCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCurrency = (String) currencyCombo.getSelectedItem();
                // Preview panel to show example values in the selected currency
                updateCurrencyPreview(newCurrency, currencyPanel);
            }
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
        
        // 3. Notification preferences
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setBackground(Color.WHITE);
        notificationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notificationPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel notificationLabel = new JLabel("Notification Preferences");
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        notificationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel notificationOptionsPanel = new JPanel(new GridLayout(4, 1));
        notificationOptionsPanel.setBackground(Color.WHITE);
        notificationOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox transactionAlertCheckbox = new JCheckBox("Transaction Alerts");
        transactionAlertCheckbox.setFont(CONTENT_FONT);
        transactionAlertCheckbox.setBackground(Color.WHITE);
        transactionAlertCheckbox.setSelected(currentUser != null ? currentUser.isTransactionAlerts() : true);
        
        JCheckBox budgetAlertCheckbox = new JCheckBox("Budget Alerts");
        budgetAlertCheckbox.setFont(CONTENT_FONT);
        budgetAlertCheckbox.setBackground(Color.WHITE);
        budgetAlertCheckbox.setSelected(currentUser != null ? currentUser.isBudgetAlerts() : true);
        
        JCheckBox billReminderCheckbox = new JCheckBox("Bill Payment Reminders");
        billReminderCheckbox.setFont(CONTENT_FONT);
        billReminderCheckbox.setBackground(Color.WHITE);
        billReminderCheckbox.setSelected(currentUser != null ? currentUser.isBillReminders() : true);
        
        JCheckBox financialTipsCheckbox = new JCheckBox("Financial Tips & Advice");
        financialTipsCheckbox.setFont(CONTENT_FONT);
        financialTipsCheckbox.setBackground(Color.WHITE);
        financialTipsCheckbox.setSelected(currentUser != null ? currentUser.isFinancialTips() : true);
        
        notificationOptionsPanel.add(transactionAlertCheckbox);
        notificationOptionsPanel.add(budgetAlertCheckbox);
        notificationOptionsPanel.add(billReminderCheckbox);
        notificationOptionsPanel.add(financialTipsCheckbox);
        
        notificationPanel.add(notificationLabel);
        notificationPanel.add(Box.createVerticalStrut(10));
        notificationPanel.add(notificationOptionsPanel);
        
        // 4. Data privacy settings
        JPanel dataPrivacyPanel = new JPanel();
        dataPrivacyPanel.setLayout(new BoxLayout(dataPrivacyPanel, BoxLayout.Y_AXIS));
        dataPrivacyPanel.setBackground(Color.WHITE);
        dataPrivacyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dataPrivacyPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel dataPrivacyLabel = new JLabel("Data Privacy");
        dataPrivacyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dataPrivacyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel dataPrivacyOptionsPanel = new JPanel(new GridLayout(2, 1));
        dataPrivacyOptionsPanel.setBackground(Color.WHITE);
        dataPrivacyOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox dataAnalyticsCheckbox = new JCheckBox("Allow Data Analytics for Personalized Recommendations");
        dataAnalyticsCheckbox.setFont(CONTENT_FONT);
        dataAnalyticsCheckbox.setBackground(Color.WHITE);
        dataAnalyticsCheckbox.setSelected(currentUser != null ? currentUser.isAllowDataAnalytics() : true);
        
        JCheckBox anonymousDataCheckbox = new JCheckBox("Share Anonymous Usage Data to Improve Services");
        anonymousDataCheckbox.setFont(CONTENT_FONT);
        anonymousDataCheckbox.setBackground(Color.WHITE);
        anonymousDataCheckbox.setSelected(currentUser != null ? currentUser.isShareAnonymousData() : true);
        
        dataPrivacyOptionsPanel.add(dataAnalyticsCheckbox);
        dataPrivacyOptionsPanel.add(anonymousDataCheckbox);
        
        dataPrivacyPanel.add(dataPrivacyLabel);
        dataPrivacyPanel.add(Box.createVerticalStrut(10));
        dataPrivacyPanel.add(dataPrivacyOptionsPanel);
        
        // Add save button
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
                
                // Save notification preferences
                currentUser.setTransactionAlerts(transactionAlertCheckbox.isSelected());
                currentUser.setBudgetAlerts(budgetAlertCheckbox.isSelected());
                currentUser.setBillReminders(billReminderCheckbox.isSelected());
                currentUser.setFinancialTips(financialTipsCheckbox.isSelected());
                
                // Save data privacy preferences
                currentUser.setAllowDataAnalytics(dataAnalyticsCheckbox.isSelected());
                currentUser.setShareAnonymousData(anonymousDataCheckbox.isSelected());
                
                // Update the user in the UserManager
                boolean saved = UserManager.getInstance().updateCurrentUser(currentUser);
                
                if (saved) {
                    // Show success message
                    JOptionPane.showMessageDialog(this, 
                        "Preferences saved successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                    // If currency changed, update the display immediately
                    if (!originalCurrency.equals(newCurrency)) {
                        updateCurrencyDisplay(newCurrency);
                    }
                } else {
                    // Show error message
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
        mainContent.add(notificationPanel);
        mainContent.add(dataPrivacyPanel);
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