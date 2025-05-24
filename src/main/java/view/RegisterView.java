package view;

import model.PasswordStrengthChecker;
import model.UserManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
    
    // Password strength components
    private JPanel passwordStrengthPanel;
    private JProgressBar strengthBar;
    private JLabel strengthLabel;
    private JTextArea requirementsTextArea;
    
    // Colors and styling
    private final Color PRIMARY_BLUE = new Color(52, 152, 219);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color WEAK_RED = new Color(231, 76, 60);
    private final Color FAIR_ORANGE = new Color(230, 126, 34);
    private final Color GOOD_YELLOW = new Color(241, 196, 15);
    private final Color STRONG_GREEN = new Color(46, 204, 113);
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 22);
    private final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    
    /**
     * Constructor for the RegisterView
     */
    public RegisterView() {
        setTitle("Finance Tracker - Register");
        setSize(450, 700); // Increased height for password strength indicator
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        createRegisterPanel();
        
        setLocationRelativeTo(null);
        setVisible(true);
        
        // 应用红黄主题
        AccountView.applyThemeToAllComponents(this);
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
        
        // Create password strength indicator panel
        createPasswordStrengthPanel();
        
        // Add password strength evaluation listener
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePasswordStrength();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePasswordStrength();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePasswordStrength();
            }
        });
        
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
                register();
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
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(passwordStrengthPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(confirmPasswordPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(loginPanel);
        
        // Add scrolling for the form
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
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
     * Creates the password strength indicator panel
     */
    private void createPasswordStrengthPanel() {
        passwordStrengthPanel = new JPanel();
        passwordStrengthPanel.setLayout(new BoxLayout(passwordStrengthPanel, BoxLayout.Y_AXIS));
        passwordStrengthPanel.setBackground(Color.WHITE);
        passwordStrengthPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordStrengthPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Progress bar for password strength
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(false);
        strengthBar.setBackground(Color.WHITE);
        strengthBar.setForeground(WEAK_RED);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        
        // Label for strength text
        strengthLabel = new JLabel("Password Strength: Weak");
        strengthLabel.setFont(new Font("Arial", Font.BOLD, 12));
        strengthLabel.setForeground(WEAK_RED);
        strengthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Text area for password requirements
        requirementsTextArea = new JTextArea();
        requirementsTextArea.setText("Password must:\n- Be at least 8 characters long\n- Contain at least one letter\n- Contain at least one number\n- Contain at least one special character");
        requirementsTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        requirementsTextArea.setEditable(false);
        requirementsTextArea.setBackground(new Color(250, 250, 250));
        requirementsTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        requirementsTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        requirementsTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Add components to panel
        passwordStrengthPanel.add(strengthBar);
        passwordStrengthPanel.add(Box.createVerticalStrut(5));
        passwordStrengthPanel.add(strengthLabel);
        passwordStrengthPanel.add(Box.createVerticalStrut(10));
        passwordStrengthPanel.add(requirementsTextArea);
    }
    
    /**
     * Updates the password strength indicator based on the current password
     */
    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        int strength = PasswordStrengthChecker.checkStrength(password);
        String strengthText = PasswordStrengthChecker.getStrengthText(strength);
        
        // Update UI based on strength
        switch (strength) {
            case PasswordStrengthChecker.STRENGTH_WEAK:
                strengthBar.setValue(20);
                strengthBar.setForeground(WEAK_RED);
                strengthLabel.setText("Password Strength: " + strengthText);
                strengthLabel.setForeground(WEAK_RED);
                break;
            case PasswordStrengthChecker.STRENGTH_FAIR:
                strengthBar.setValue(40);
                strengthBar.setForeground(FAIR_ORANGE);
                strengthLabel.setText("Password Strength: " + strengthText);
                strengthLabel.setForeground(FAIR_ORANGE);
                break;
            case PasswordStrengthChecker.STRENGTH_GOOD:
                strengthBar.setValue(60);
                strengthBar.setForeground(GOOD_YELLOW);
                strengthLabel.setText("Password Strength: " + strengthText);
                strengthLabel.setForeground(GOOD_YELLOW);
                break;
            case PasswordStrengthChecker.STRENGTH_STRONG:
                strengthBar.setValue(80);
                strengthBar.setForeground(STRONG_GREEN);
                strengthLabel.setText("Password Strength: " + strengthText);
                strengthLabel.setForeground(STRONG_GREEN);
                break;
            case PasswordStrengthChecker.STRENGTH_VERY_STRONG:
                strengthBar.setValue(100);
                strengthBar.setForeground(STRONG_GREEN);
                strengthLabel.setText("Password Strength: " + strengthText);
                strengthLabel.setForeground(STRONG_GREEN);
                break;
        }
        
        // Show any failed requirements
        if (!PasswordStrengthChecker.meetsMinimumRequirements(password)) {
            requirementsTextArea.setText(PasswordStrengthChecker.getFailedRequirements(password));
        } else {
            requirementsTextArea.setText("All requirements met. Your password is " + strengthText.toLowerCase() + ".");
        }
    }
    
    /**
     * Handles the registration process
     */
    private void register() {
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
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                RegisterView.this,
                "Passwords do not match",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        if (!PasswordStrengthChecker.meetsMinimumRequirements(password)) {
            JOptionPane.showMessageDialog(
                RegisterView.this,
                "Password does not meet minimum requirements:\n" + 
                PasswordStrengthChecker.getFailedRequirements(password),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Attempt to register the user
        UserManager userManager = UserManager.getInstance();
        if (userManager.registerUser(username, password, email)) {
            JOptionPane.showMessageDialog(
                RegisterView.this,
                "Registration successful! Please login.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            dispose(); // Close registration window
            new LoginView(); // Open login view
        } else {
            JOptionPane.showMessageDialog(
                RegisterView.this,
                "Username already exists or registration failed.",
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
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