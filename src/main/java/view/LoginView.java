package view;

import model.SessionManager;
import model.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * LoginView - The login screen of the application.
 * This class displays a login screen and transitions to other views upon successful login.
 */
public class LoginView extends JFrame {
    
    // UI Components
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;

    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    /**
     * Constructor for the LoginView
     */
    public LoginView() {
        setTitle("Finance Tracker - Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createLoginPanel();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // 应用红黄主题
        AccountView.applyThemeToAllComponents(this);
    }
    
    /**
     * Creates the login panel
     */
    private void createLoginPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // App Logo/Title
        JLabel titleLabel = new JLabel("Finance Tracker");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(PRIMARY_BLUE);
        
        // Center-aligned panels for username and password
        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Username field with centered label
        JPanel usernameLabelPanel = new JPanel();
        usernameLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        usernameLabelPanel.setBackground(Color.WHITE);
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(LABEL_FONT);
        usernameLabelPanel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setFont(LABEL_FONT);
        
        usernamePanel.add(usernameLabelPanel, BorderLayout.NORTH);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        
        // Password field with centered label
        JPanel passwordLabelPanel = new JPanel();
        passwordLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        passwordLabelPanel.setBackground(Color.WHITE);
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabelPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(LABEL_FONT);
        
        passwordPanel.add(passwordLabelPanel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(PRIMARY_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(150, 40));
        loginButton.setFocusPainted(false);
        
        // Make sure button has opaque background to display correctly
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        
        // Register link
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel registerLabel = new JLabel("Don't have an account? ");
        registerLabel.setFont(LABEL_FONT);
        
        JLabel registerLink = new JLabel("Register");
        registerLink.setFont(LABEL_FONT);
        registerLink.setForeground(PRIMARY_BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add click event for register link
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose(); // Close login window
                new RegisterView(); // Open registration view
            }
        });
        
        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);
        
        // Add action listener to login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Add components to the panel with spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(registerPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Handles the login authentication
     */
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Basic validation
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                LoginView.this,
                "Please enter both username and password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Authenticate user
        UserManager userManager = UserManager.getInstance();
        if (userManager.authenticateUser(username, password)) {
            // Show welcome message
            JOptionPane.showMessageDialog(
                LoginView.this,
                "Welcome, " + username + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // Navigate to main view
            dispose(); // Close the login window
            AccountView accountView = new AccountView();
            
            // Start session monitoring
            SessionManager.getInstance().startSession(accountView);
        } else {
            JOptionPane.showMessageDialog(
                LoginView.this,
                "Invalid username or password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Main method to run the application
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
                new LoginView();
            }
        });
    }
}
