package model;

import java.util.regex.Pattern;

/**
 * PasswordStrengthChecker - Utility class to check password strength.
 * Evaluates password complexity against industry-standard rules.
 */
public class PasswordStrengthChecker {

    // Password strength levels
    public static final int STRENGTH_WEAK = 0;
    public static final int STRENGTH_FAIR = 1;
    public static final int STRENGTH_GOOD = 2;
    public static final int STRENGTH_STRONG = 3;
    public static final int STRENGTH_VERY_STRONG = 4;
    
    // Minimum requirements
    public static final int MIN_LENGTH = 8;
    public static final int STRONG_LENGTH = 12;
    
    /**
     * Checks the strength of a password
     * @param password The password to check
     * @return An integer representing password strength (0-4)
     */
    public static int checkStrength(String password) {
        if (password == null || password.isEmpty()) {
            return STRENGTH_WEAK;
        }
        
        int score = 0;
        
        // Check password length
        if (password.length() >= MIN_LENGTH) {
            score++;
        }
        if (password.length() >= STRONG_LENGTH) {
            score++;
        }
        
        // Check for numbers
        if (Pattern.compile("[0-9]").matcher(password).find()) {
            score++;
        }
        
        // Check for lowercase letters
        if (Pattern.compile("[a-z]").matcher(password).find()) {
            score++;
        }
        
        // Check for uppercase letters
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            score++;
        }
        
        // Check for special characters
        if (Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            score++;
        }
        
        // Adjust final score based on total checks
        if (score < 3) {
            return STRENGTH_WEAK;
        } else if (score < 4) {
            return STRENGTH_FAIR;
        } else if (score < 5) {
            return STRENGTH_GOOD;
        } else if (score < 6) {
            return STRENGTH_STRONG;
        } else {
            return STRENGTH_VERY_STRONG;
        }
    }
    
    /**
     * Gets a textual representation of password strength
     * @param strengthLevel The strength level (0-4)
     * @return A string describing the password strength
     */
    public static String getStrengthText(int strengthLevel) {
        switch (strengthLevel) {
            case STRENGTH_WEAK:
                return "Weak";
            case STRENGTH_FAIR:
                return "Fair";
            case STRENGTH_GOOD:
                return "Good";
            case STRENGTH_STRONG:
                return "Strong";
            case STRENGTH_VERY_STRONG:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }
    
    /**
     * Checks if a password meets minimum requirements
     * @param password The password to check
     * @return True if the password meets minimum requirements, false otherwise
     */
    public static boolean meetsMinimumRequirements(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        
        boolean hasLetter = Pattern.compile("[a-zA-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        
        return hasLetter && hasDigit && hasSpecial;
    }
    
    /**
     * Gets a list of requirements that the password doesn't meet
     * @param password The password to check
     * @return A string with the list of unmet requirements
     */
    public static String getFailedRequirements(String password) {
        StringBuilder requirements = new StringBuilder();
        
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < MIN_LENGTH) {
            requirements.append("- Password must be at least ").append(MIN_LENGTH).append(" characters long\n");
        }
        
        if (!Pattern.compile("[a-zA-Z]").matcher(password).find()) {
            requirements.append("- Password must contain at least one letter\n");
        }
        
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            requirements.append("- Password must contain at least one number\n");
        }
        
        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            requirements.append("- Password must contain at least one special character\n");
        }
        
        return requirements.toString();
    }
} 