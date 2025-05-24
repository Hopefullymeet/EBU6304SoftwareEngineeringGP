package view;

import model.DeepSeekAPI;
import model.SessionManager;
import model.UserManager;
import model.User;
import model.CurrencyManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FinancialAdvisorView - The AI financial advisor chat interface.
 * This class provides a chat-based interface for users to get financial advice.
 */
public class FinancialAdvisorView extends JFrame {
    
    // UI Components
    private JPanel contentPanel;
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private JScrollPane scrollPane;
    private JPanel headerPanel;
    private JPanel sidebarPanel;
    private JPanel footerPanel;
    
    // API Key field for configuration
    private JTextField apiKeyField;
    private JButton connectButton;
    private JLabel statusLabel;
    
    // DeepSeek API client
    private DeepSeekAPI deepSeekAPI;
    private boolean isConnected = false;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color DARK_GRAY = new Color(100, 100, 100);
    private final Color USER_MESSAGE_COLOR = new Color(230, 246, 255);
    private final Color AI_MESSAGE_COLOR = new Color(240, 240, 240);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font CONTENT_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font SIDEBAR_FONT = new Font("Arial", Font.PLAIN, 14);
    
    // Managers
    private CurrencyManager currencyManager;
    
    private User currentUser;
    
    /**
     * Constructor for the FinancialAdvisorView
     */
    public FinancialAdvisorView() {
        setTitle("Financial Advisor");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Initialize managers
        currencyManager = CurrencyManager.getInstance();
        
        createHeader();
        createSidebar();
        createChatInterface();
        createFooter();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Set initial focus to the API key field
        apiKeyField.requestFocusInWindow();
        
        // Start session monitoring
        SessionManager.getInstance().startSession(this);
        
        // Apply CNY theme if enabled
        if (AccountView.isCNYTheme) {
            applyCNYTheme();
        }
    }
    
    /**
     * Applies the CNY theme to all components
     */
    private void applyCNYTheme() {
        // Apply theme to the entire frame components
        applyThemeToComponent(this.getContentPane());
        
        // Ensure specific panels are themed correctly
        if (headerPanel != null) {
            headerPanel.setBackground(AccountView.CNY_RED);
            headerPanel.repaint();
        }
        
        if (sidebarPanel != null) {
            sidebarPanel.setBackground(AccountView.CNY_RED);
            sidebarPanel.repaint();
        }
        
        if (contentPanel != null) {
            contentPanel.setBackground(AccountView.CNY_RED);
            contentPanel.repaint();
        }
        
        if (footerPanel != null) {
            footerPanel.setBackground(AccountView.CNY_RED);
            footerPanel.repaint();
        }
        
        repaint();
    }
    
    /**
     * Recursively applies the CNY theme to components
     * @param component The component to update
     */
    private void applyThemeToComponent(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBackground(AccountView.CNY_RED);
            
            // Process child components
            for (Component child : panel.getComponents()) {
                applyThemeToComponent(child);
            }
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            // Special case for logout button
            if (button.getText().equals("Logout")) {
                button.setBackground(new Color(231, 76, 60)); // Keep red
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(AccountView.CNY_YELLOW);
                button.setForeground(AccountView.CNY_RED);
            }
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            // Special case for header title
            if (label.getFont().getSize() >= 20) {
                label.setForeground(AccountView.CNY_YELLOW);
            } else {
                label.setForeground(AccountView.CNY_YELLOW);
            }
        } else if (component instanceof JTextField) {
            component.setBackground(Color.WHITE); // Keep white for readability
        } else if (component instanceof JRadioButton) {
            JRadioButton radioButton = (JRadioButton) component;
            radioButton.setBackground(AccountView.CNY_RED);
            radioButton.setForeground(AccountView.CNY_YELLOW);
        } else if (component instanceof JTextPane) {
            // Don't change background of text fields/panes for readability
            component.setBackground(Color.WHITE);
        } else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.getViewport().setBackground(AccountView.CNY_RED);
            
            // Process the viewport's view
            Component view = scrollPane.getViewport().getView();
            if (view != null) {
                if (view instanceof JTextPane) {
                    // Don't change text pane background for readability
                    view.setBackground(Color.WHITE);
                } else {
                    view.setBackground(AccountView.CNY_RED);
                }
            }
        }
    }
    
    /**
     * Creates the header panel with title and logout button
     */
    private void createHeader() {
        headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setLayout(new BorderLayout());
        
        // Create back button in the top-left corner
        JButton backButton = new JButton("← Back");
        backButton.setBackground(PRIMARY_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        
        // Create a panel to hold the back button with some margin
        JPanel backPanel = new JPanel();
        backPanel.setBackground(PRIMARY_BLUE);
        backPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        backPanel.add(backButton);
        
        // Add action listener to back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close current window
                new AccountView(); // Return to account view
            }
        });
        
        JLabel titleLabel = new JLabel("Financial Advisor");
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
        
        headerPanel.add(backPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the sidebar with configuration options
     */
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        // API Configuration section
        JLabel configLabel = new JLabel("Configuration");
        configLabel.setFont(new Font("Arial", Font.BOLD, 16));
        configLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel apiKeyLabel = new JLabel("DeepSeek API Key:");
        apiKeyLabel.setFont(CONTENT_FONT);
        apiKeyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        apiKeyLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        // Create API key field with placeholder text
        apiKeyField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Add placeholder text if the field is empty and doesn't have focus
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("default", getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2.dispose();
                }
            }
        };
        apiKeyField.setFont(CONTENT_FONT);
        apiKeyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        apiKeyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Model selection
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setFont(CONTENT_FONT);
        modelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        modelLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
        
        // Create radio buttons for model selection
        JRadioButton chatModelButton = new JRadioButton("DeepSeek-Chat (V3)");
        chatModelButton.setFont(CONTENT_FONT);
        chatModelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatModelButton.setBackground(Color.WHITE);
        chatModelButton.setSelected(true); // Default selection
        
        JRadioButton reasonerModelButton = new JRadioButton("DeepSeek-Reasoner (R1)");
        reasonerModelButton.setFont(CONTENT_FONT);
        reasonerModelButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        reasonerModelButton.setBackground(Color.WHITE);
        
        // Group radio buttons
        ButtonGroup modelGroup = new ButtonGroup();
        modelGroup.add(chatModelButton);
        modelGroup.add(reasonerModelButton);
        
        // Add tooltip descriptions for models
        chatModelButton.setToolTipText("General-purpose chat model with broad knowledge");
        reasonerModelButton.setToolTipText("Better for logical reasoning and complex financial analysis");
        
        connectButton = new JButton("Connect");
        connectButton.setFont(CONTENT_FONT);
        connectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        connectButton.setMargin(new Insets(8, 15, 8, 15));
        connectButton.setFocusPainted(false);
        connectButton.setBackground(PRIMARY_BLUE);
        connectButton.setForeground(Color.WHITE);
        connectButton.setOpaque(true);
        connectButton.setBorderPainted(false);
        connectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        connectButton.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        statusLabel = new JLabel("Status: Not Connected");
        statusLabel.setFont(CONTENT_FONT);
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        
        // Add action listener to connect button
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    String apiKey = apiKeyField.getText().trim();
                    
                    try {
                        // Determine which model is selected
                        String selectedModel = chatModelButton.isSelected() ? 
                            DeepSeekAPI.MODEL_CHAT : DeepSeekAPI.MODEL_REASONER;
                        
                        // Initialize API client - use default key if no key provided
                        if (apiKey.isEmpty()) {
                            // Use default API Key
                            deepSeekAPI = new DeepSeekAPI();
                            deepSeekAPI.setModel(selectedModel);
                        } else {
                            // Use provided API Key
                            deepSeekAPI = new DeepSeekAPI(apiKey, selectedModel);
                        }
                        
                        isConnected = true;
                        statusLabel.setText("Status: Connected to " + 
                            (selectedModel.equals(DeepSeekAPI.MODEL_CHAT) ? "DeepSeek-Chat" : "DeepSeek-Reasoner"));
                        statusLabel.setForeground(new Color(46, 204, 113)); // Green
                        connectButton.setText("Disconnect");
                        apiKeyField.setEnabled(false);
                        chatModelButton.setEnabled(false);
                        reasonerModelButton.setEnabled(false);
                        
                        // Add welcome message
                        addAIMessage("Hello! I'm your AI financial advisor. How can I help you with your finances today?");
                        
                        // Enable chat interface
                        inputField.setEnabled(true);
                        sendButton.setEnabled(true);
                        inputField.requestFocusInWindow();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(FinancialAdvisorView.this, 
                            "Failed to connect: " + ex.getMessage(), 
                            "Connection Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Disconnect
                    isConnected = false;
                    statusLabel.setText("Status: Not Connected");
                    statusLabel.setForeground(Color.RED);
                    connectButton.setText("Connect");
                    apiKeyField.setEnabled(true);
                    chatModelButton.setEnabled(true);
                    reasonerModelButton.setEnabled(true);
                    
                    // Disable chat interface
                    inputField.setEnabled(false);
                    sendButton.setEnabled(false);
                    
                    // Clear chat
                    clearChat();
                }
            }
        });
        
        // Add components to sidebar
        sidebarPanel.add(configLabel);
        sidebarPanel.add(apiKeyLabel);
        sidebarPanel.add(apiKeyField);
        sidebarPanel.add(modelLabel);
        sidebarPanel.add(chatModelButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(reasonerModelButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(connectButton);
        sidebarPanel.add(statusLabel);
        
        // Chat actions section
        JLabel actionsLabel = new JLabel("Actions");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        actionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsLabel.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        JButton clearButton = new JButton("Clear Chat");
        clearButton.setFont(CONTENT_FONT);
        clearButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        clearButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        clearButton.setMargin(new Insets(8, 15, 8, 15));
        clearButton.setFocusPainted(false);
        
        // Add action listener to clear button
        clearButton.addActionListener(e -> {
            if (isConnected) {
                deepSeekAPI.clearConversation();
            }
            clearChat();
        });
        
        // Help section
        JLabel helpLabel = new JLabel("Topic Suggestions");
        helpLabel.setFont(new Font("Arial", Font.BOLD, 16));
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        helpLabel.setBorder(new EmptyBorder(20, 0, 5, 0));
        
        // Create suggestion buttons
        String[] suggestions = {
            "Saving for retirement",
            "Investment strategies",
            "Budgeting tips",
            "Debt management",
            "Tax planning"
        };
        
        // First add the actionsLabel and clearButton to the sidebar
        sidebarPanel.add(actionsLabel);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(clearButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(helpLabel);
        
        // Then add suggestion buttons
        for (String suggestion : suggestions) {
            JButton suggestionButton = createSuggestionButton(suggestion);
            sidebarPanel.add(suggestionButton);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }
        
        // Add sidebar to main frame
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    /**
     * Creates a suggestion button for quick topics
     * @param text The text for the button
     * @return The created button
     */
    private JButton createSuggestionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(CONTENT_FONT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setMargin(new Insets(5, 10, 5, 10));
        button.setBackground(LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Add action listener to suggestion button
        button.addActionListener(e -> {
            if (isConnected) {
                String question = text + "?";
                sendMessage(question);
            } else {
                JOptionPane.showMessageDialog(FinancialAdvisorView.this, 
                    "Please connect to the API first", 
                    "Not Connected", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        return button;
    }
    
    /**
     * Creates the main chat interface
     */
    private void createChatInterface() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Create chat pane with styled document
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(CONTENT_FONT);
        chatPane.setBackground(Color.WHITE);
        
        // Create scroll pane for chat
        scrollPane = new JScrollPane(chatPane);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the footer with message input
     */
    private void createFooter() {
        footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Input field
        inputField = new JTextField();
        inputField.setFont(CONTENT_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        inputField.setEnabled(false); // Initially disabled until connected
        
        // Add key listener to input field for Enter key
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(inputField.getText());
                }
            }
        });
        
        // Send button
        sendButton = new JButton("Send");
        sendButton.setFont(CONTENT_FONT);
        sendButton.setBackground(PRIMARY_BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setEnabled(false); // Initially disabled until connected
        
        // Add action listener to send button
        sendButton.addActionListener(e -> sendMessage(inputField.getText()));
        
        footerPanel.add(inputField, BorderLayout.CENTER);
        footerPanel.add(sendButton, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sends a message to the AI and updates the chat
     * @param message The message to send
     */
    private void sendMessage(String message) {
        if (message.trim().isEmpty() || !isConnected) {
            return;
        }
        
        // Add user message to chat
        addUserMessage(message);
        
        // Clear input field
        inputField.setText("");
        
        // Make sure we have the AI style created before adding the empty message
        if (chatPane.getStyle("AIStyle") == null) {
            Style aiStyle = chatPane.addStyle("AIStyle", null);
            StyleConstants.setBackground(aiStyle, AI_MESSAGE_COLOR);
            StyleConstants.setForeground(aiStyle, Color.BLACK);
            StyleConstants.setFontFamily(aiStyle, "Arial");
            StyleConstants.setFontSize(aiStyle, 14);
        }
        
        // Add an empty AI message that will be filled in with streaming content
        String timestamp = getCurrentTimestamp();
        addEmptyAIMessage(timestamp);
        
        // Send message to API with streaming
        deepSeekAPI.sendMessage(message, chunk -> {
            // Append new content as it arrives
            SwingUtilities.invokeLater(() -> {
                appendToLastAIMessage(chunk);
            });
        });
    }
    
    /**
     * Adds an empty AI message to the chat that will be filled in with streaming content
     * @param timestamp The timestamp to use for the message
     */
    private void addEmptyAIMessage(String timestamp) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Add timestamp and sender
            Style timestampStyle = chatPane.addStyle("Timestamp", null);
            StyleConstants.setForeground(timestampStyle, DARK_GRAY);
            StyleConstants.setFontSize(timestampStyle, 10);
            doc.insertString(doc.getLength(), timestamp + " - Financial Advisor\n", timestampStyle);
            
            // Create the paragraph attributes for proper formatting
            SimpleAttributeSet paragraphAttrs = new SimpleAttributeSet();
            StyleConstants.setBackground(paragraphAttrs, AI_MESSAGE_COLOR);
            StyleConstants.setLeftIndent(paragraphAttrs, 5);
            StyleConstants.setRightIndent(paragraphAttrs, 5);
            StyleConstants.setSpaceAbove(paragraphAttrs, 3);
            StyleConstants.setSpaceBelow(paragraphAttrs, 3);
            
            // Apply paragraph attributes for the incoming content
            int pos = doc.getLength();
            doc.setParagraphAttributes(pos, 1, paragraphAttrs, false);
            
            // Scroll to bottom
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Appends content to the last AI message (for streaming responses)
     * @param content The content to append
     */
    private void appendToLastAIMessage(String content) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Check for the special stream done flag
            if (content.contains("__STREAM_DONE__")) {
                // This is the end of the message, ensure we have double newlines
                int docLength = doc.getLength();
                String endText = "";
                
                // Check if we have enough characters to check for newlines
                if (docLength >= 2) {
                    endText = doc.getText(docLength - 2, 2);
                }
                
                // Add newlines if needed
                Style aiStyle = chatPane.getStyle("AIStyle");
                if (!endText.equals("\n\n")) {
                    // Remove any partial __STREAM_DONE__ that might have been added
                    String docText = doc.getText(0, doc.getLength());
                    int streamDoneIndex = docText.lastIndexOf("__STREAM_DONE__");
                    if (streamDoneIndex >= 0) {
                        doc.remove(streamDoneIndex, "__STREAM_DONE__".length());
                    }
                    
                    // Add the double newlines
                    doc.insertString(doc.getLength(), "\n\n", aiStyle);
                }
                
                return;
            }
            
            // Get or create style for AI message if not exists
            Style aiStyle = chatPane.getStyle("AIStyle");
            if (aiStyle == null) {
                aiStyle = chatPane.addStyle("AIStyle", null);
                StyleConstants.setBackground(aiStyle, AI_MESSAGE_COLOR);
                StyleConstants.setForeground(aiStyle, Color.BLACK);
                StyleConstants.setFontFamily(aiStyle, "Arial");
                StyleConstants.setFontSize(aiStyle, 14);
            }
            
            // Insert the content at the end of the document
            int insertPosition = doc.getLength();
            doc.insertString(insertPosition, content, aiStyle);
            
            // Create the paragraph attributes for proper formatting
            SimpleAttributeSet paragraphAttrs = new SimpleAttributeSet();
            StyleConstants.setBackground(paragraphAttrs, AI_MESSAGE_COLOR);
            StyleConstants.setLeftIndent(paragraphAttrs, 5);
            StyleConstants.setRightIndent(paragraphAttrs, 5);
            StyleConstants.setSpaceAbove(paragraphAttrs, 3);
            StyleConstants.setSpaceBelow(paragraphAttrs, 3);
            
            // Re-apply paragraph attributes to ensure consistent formatting
            int startPos = doc.getLength() - content.length();
            if (startPos < 0) startPos = 0;
            doc.setParagraphAttributes(startPos, content.length(), paragraphAttrs, false);
            
            // Check if we need to add the final double line break at the end of the message
            if (content.endsWith(".") || content.endsWith("?") || content.endsWith("!") || 
                content.endsWith("。") || content.endsWith("？") || content.endsWith("！")) {
                // Check if we already have newlines at the end
                int docLength = doc.getLength();
                String endText = "";
                if (docLength >= 2) {
                    endText = doc.getText(docLength - 2, 2);
                }
                if (!endText.equals("\n\n")) {
                    doc.insertString(docLength, "\n\n", aiStyle);
                }
            }
            
            // Scroll to bottom
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds a typing indicator to the chat
     * @return The id of the typing indicator for removal
     */
    private String addTypingIndicator() {
        // This method is no longer used with streaming - keeping for compatibility
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Add timestamp and sender
            String timestamp = getCurrentTimestamp();
            Style timestampStyle = chatPane.addStyle("Timestamp", null);
            StyleConstants.setForeground(timestampStyle, DARK_GRAY);
            StyleConstants.setFontSize(timestampStyle, 10);
            doc.insertString(doc.getLength(), timestamp + " - Financial Advisor\n", timestampStyle);
            
            // Add typing indicator with background color
            Style aiStyle = chatPane.addStyle("TypingStyle", null);
            StyleConstants.setBackground(aiStyle, AI_MESSAGE_COLOR);
            StyleConstants.setForeground(aiStyle, Color.DARK_GRAY);
            StyleConstants.setFontFamily(aiStyle, "Arial");
            StyleConstants.setFontSize(aiStyle, 14);
            StyleConstants.setItalic(aiStyle, true);
            
            // Insert message with padding
            doc.insertString(doc.getLength(), "Typing...\n\n", aiStyle);
            
            // Scroll to bottom
            scrollToBottom();
            
            return timestamp;
        } catch (BadLocationException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adds a user message to the chat
     * @param message The message to add
     */
    private void addUserMessage(String message) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Add timestamp and sender
            String timestamp = getCurrentTimestamp();
            Style timestampStyle = chatPane.addStyle("Timestamp", null);
            StyleConstants.setForeground(timestampStyle, DARK_GRAY);
            StyleConstants.setFontSize(timestampStyle, 10);
            doc.insertString(doc.getLength(), timestamp + " - You\n", timestampStyle);
            
            // Add message with background color
            Style userStyle = chatPane.addStyle("UserStyle", null);
            StyleConstants.setBackground(userStyle, USER_MESSAGE_COLOR);
            StyleConstants.setForeground(userStyle, Color.BLACK);
            StyleConstants.setFontFamily(userStyle, "Arial");
            StyleConstants.setFontSize(userStyle, 14);
            
            // Create a message panel style for padding
            SimpleAttributeSet messageAttr = new SimpleAttributeSet();
            StyleConstants.setSpaceAbove(messageAttr, 5);
            StyleConstants.setSpaceBelow(messageAttr, 10);
            
            // Insert message with padding
            doc.insertString(doc.getLength(), message + "\n\n", userStyle);
            
            // Scroll to bottom
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds an AI message to the chat
     * @param message The message to add
     */
    private void addAIMessage(String message) {
        try {
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Add timestamp and sender
            String timestamp = getCurrentTimestamp();
            Style timestampStyle = chatPane.addStyle("Timestamp", null);
            StyleConstants.setForeground(timestampStyle, DARK_GRAY);
            StyleConstants.setFontSize(timestampStyle, 10);
            doc.insertString(doc.getLength(), timestamp + " - Financial Advisor\n", timestampStyle);
            
            // Add message with background color
            Style aiStyle = chatPane.addStyle("AIStyle", null);
            StyleConstants.setBackground(aiStyle, AI_MESSAGE_COLOR);
            StyleConstants.setForeground(aiStyle, Color.BLACK);
            StyleConstants.setFontFamily(aiStyle, "Arial");
            StyleConstants.setFontSize(aiStyle, 14);
            
            // Create a message panel style for padding
            SimpleAttributeSet messageAttr = new SimpleAttributeSet();
            StyleConstants.setSpaceAbove(messageAttr, 5);
            StyleConstants.setSpaceBelow(messageAttr, 10);
            
            // Insert message with padding
            doc.insertString(doc.getLength(), message + "\n\n", aiStyle);
            
            // Scroll to bottom
            scrollToBottom();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Clears the chat pane
     */
    private void clearChat() {
        chatPane.setText("");
    }
    
    /**
     * Scrolls the chat pane to the bottom
     */
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    /**
     * Gets the current timestamp
     * @return The formatted timestamp
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
    
    /**
     * Creates the investment recommendations content
     */
    private void createInvestmentRecommendations() {
        // Get user's currency preference
        User currentUser = UserManager.getInstance().getCurrentUser();
        String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
        
        // Convert and format any monetary values in this panel
        // Example for an investment amount:
        double investmentAmountCNY = 10000.00; // Base amount in CNY
        double convertedAmount = currencyManager.convert(investmentAmountCNY, CurrencyManager.CNY, userCurrency);
        String formattedAmount = currencyManager.format(convertedAmount, userCurrency);
        
        // Update labels to use the formatted values
        JLabel investmentLabel = new JLabel("Recommended Investment: " + formattedAmount);
    }
    
    /**
     * Adds an investment opportunity card
     */
    private void addInvestmentCard(JPanel panel, String name, String type, double minInvestment, double expectedReturn) {
        // Get user's currency preference
        User currentUser = UserManager.getInstance().getCurrentUser();
        String userCurrency = currentUser != null ? currentUser.getCurrency() : CurrencyManager.CNY;
        
        // Convert minimum investment to user's preferred currency
        double convertedMinInvestment = currencyManager.convert(minInvestment, CurrencyManager.CNY, userCurrency);
        
        // Format the amount according to user's currency
        String formattedMinInvestment = currencyManager.format(convertedMinInvestment, userCurrency);
        
        // ... existing card creation code ...
        
        // Update labels to use the formatted values
        JLabel investmentLabel = new JLabel("Min. Investment: " + formattedMinInvestment);
        
        // ... rest of the method ...
    }
    
    /**
     * Refreshes all monetary displays to reflect the current user's currency preference
     */
    private void refreshCurrencyDisplay() {
        // Refresh all panels that display monetary values
        createInvestmentRecommendations();
        // ... other panels that need refreshing
    }
    
    /**
     * Main method to test the FinancialAdvisorView
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
                new FinancialAdvisorView();
            }
        });
    }
} 