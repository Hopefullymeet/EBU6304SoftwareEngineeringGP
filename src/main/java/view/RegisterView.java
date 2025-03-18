package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RegisterView - The registration screen of the application.
 * This class displays a registration form for new users.
 */
public class RegisterView extends JFrame {
    
    // UI Components
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    
    /**
     * Constructor for the RegisterView
     */
    public RegisterView() {
        setTitle("Finance Tracker - Register");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createRegisterPanel();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Creates the registration panel
     */
    private void createRegisterPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // App Logo/Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(PRIMARY_BLUE);
        
        // Center-aligned panels for form fields
        JPanel usernamePanel = createFormFieldPanel("Username");
        JPanel emailPanel = createFormFieldPanel("Email");
        JPanel passwordPanel = createFormFieldPanel("Password");
        JPanel confirmPasswordPanel = createFormFieldPanel("Confirm Password");
        
        // Form fields
        usernameField = new JTextField();
        usernameField.setFont(LABEL_FONT);
        
        emailField = new JTextField();
        emailField.setFont(LABEL_FONT);
        
        passwordField = new JPasswordField();
        passwordField.setFont(LABEL_FONT);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(LABEL_FONT);
        
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        emailPanel.add(emailField, BorderLayout.CENTER);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        
        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(BUTTON_FONT);
        registerButton.setBackground(PRIMARY_BLUE);
        registerButton.setForeground(Color.WHITE);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(150, 40));
        registerButton.setFocusPainted(false);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        
        // Login link
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel loginLabel = new JLabel("Already have an account? ");
        loginLabel.setFont(LABEL_FONT);
        
        JLabel loginLink = new JLabel("Login");
        loginLink.setFont(LABEL_FONT);
        loginLink.setForeground(PRIMARY_BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add click event for login link
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose(); // Close registration window
                new LoginView(); // Open login view
            }
        });
        
        loginPanel.add(loginLabel);
        loginPanel.add(loginLink);
        
        // Add action listener to register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                // Validate inputs
                if (username.trim().isEmpty() || email.trim().isEmpty() || 
                    password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        RegisterView.this,
                        "All fields are required",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                } else if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(
                        RegisterView.this,
                        "Passwords do not match",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    // For demonstration, registration is always successful
                    JOptionPane.showMessageDialog(
                        RegisterView.this,
                        "Registration successful! Please login.",
                        "Registration Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose(); // Close registration window
                    new LoginView(); // Open login view
                }
            }
        });
        
        // Add components to the panel with spacing
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(confirmPasswordPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loginPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates a panel for a form field with a centered label
     * @param labelText The text for the field label
     * @return The created panel
     */
    private JPanel createFormFieldPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        labelPanel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        labelPanel.add(label);
        
        panel.add(labelPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * Main method to test the RegisterView
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
                new RegisterView();
            }
        });
    }
} 