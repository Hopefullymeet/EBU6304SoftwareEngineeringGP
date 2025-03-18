package view;

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
    
    // Account sub-items container
    private JPanel accountSubItems;
    private boolean accountExpanded = true;
    
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
    
    /**
     * Constructor for the AccountView
     */
    public AccountView() {
        setTitle("Account");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createHeader();
        createSidebar();
        
        // Create card layout for different content panels
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(Color.WHITE);
        
        // Add different content panels to the card layout
        createAccountContent();
        createAccountsManagerContent();
        
        add(cardsPanel, BorderLayout.CENTER);
        
        // Default view is profile panel
        cardLayout.show(cardsPanel, PROFILE_PANEL);
        
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
        
        // Add personal information fields
        addFormField(personalInfoPanel, "Name:", "John Doe");
        addFormField(personalInfoPanel, "Email:", "john.doe@example.com");
        addFormField(personalInfoPanel, "Phone:", "(123) 456-7890");
        
        // Security settings section
        JLabel securityLabel = new JLabel("Security Settings");
        securityLabel.setFont(SUBHEADER_FONT);
        securityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel securityPanel = new JPanel();
        securityPanel.setLayout(new GridLayout(2, 2, 15, 25));
        securityPanel.setBackground(Color.WHITE);
        securityPanel.setBorder(new EmptyBorder(20, 0, 40, 0));
        securityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        securityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
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
        
        securityPanel.add(passwordLabel);
        securityPanel.add(passwordButtonPanel);
        
        JLabel twoFactorLabel = new JLabel("Two-Factor Authentication:");
        twoFactorLabel.setFont(CONTENT_FONT);
        
        JPanel twoFactorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        twoFactorPanel.setBackground(Color.WHITE);
        
        JToggleButton twoFactorToggle = new JToggleButton("Enable");
        twoFactorToggle.setFont(CONTENT_FONT);
        twoFactorToggle.setFocusPainted(false);
        twoFactorToggle.setPreferredSize(new Dimension(twoFactorToggle.getPreferredSize().width, 40));
        twoFactorToggle.setMargin(new Insets(8, 15, 8, 15));
        twoFactorPanel.add(twoFactorToggle);
        
        securityPanel.add(twoFactorLabel);
        securityPanel.add(twoFactorPanel);
        
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
        
        JLabel totalAssetsValue = new JLabel("$12,458.92");
        totalAssetsValue.setFont(new Font("Arial", Font.BOLD, 24));
        totalAssetsValue.setForeground(POSITIVE_GREEN);
        
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        detailsPanel.setBackground(LIGHT_GRAY);
        
        addAssetDetail(detailsPanel, "Assets", "$15,250.75", POSITIVE_GREEN);
        addAssetDetail(detailsPanel, "Debts", "-$2,791.83", NEGATIVE_RED);
        addAssetDetail(detailsPanel, "Net Worth", "$12,458.92", POSITIVE_GREEN);
        
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
        
        // Add bank accounts
        addAccountCard(accountsList, "Chase Checking", "Checking", "$2,453.65", "account-bank.png");
        addAccountCard(accountsList, "Chase Savings", "Savings", "$8,257.12", "account-bank.png");
        
        // Credit accounts
        JLabel creditAccountsLabel = new JLabel("Credit Cards");
        creditAccountsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        creditAccountsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        creditAccountsLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add credit accounts
        addAccountCard(accountsList, "Visa Rewards", "Credit Card", "-$1,853.27", "account-credit.png");
        addAccountCard(accountsList, "Amex Platinum", "Credit Card", "-$938.56", "account-credit.png");
        
        // Digital wallet accounts
        JLabel digitalWalletLabel = new JLabel("Digital Wallets");
        digitalWalletLabel.setFont(new Font("Arial", Font.BOLD, 16));
        digitalWalletLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        digitalWalletLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add digital wallet accounts
        addAccountCard(accountsList, "PayPal", "Digital Wallet", "$345.98", "account-digital.png");
        addAccountCard(accountsList, "Venmo", "Digital Wallet", "$154.23", "account-digital.png");
        
        // Investments
        JLabel investmentsLabel = new JLabel("Investments");
        investmentsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        investmentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        investmentsLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Add investment accounts
        addAccountCard(accountsList, "Vanguard 401(k)", "Retirement", "$3,452.87", "account-investment.png");
        addAccountCard(accountsList, "Robinhood", "Brokerage", "$586.90", "account-investment.png");
        
        // Button to add new account
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
