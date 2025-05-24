package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PasswordStrengthChecker class functionality
 */
public class PasswordStrengthCheckerTest {

    @ParameterizedTest
    @DisplayName("Test null or empty passwords should return weak strength")
    @NullAndEmptySource
    public void testNullOrEmptyPasswordIsWeak(String password) {
        assertEquals(PasswordStrengthChecker.STRENGTH_WEAK, 
                     PasswordStrengthChecker.checkStrength(password),
                     "Null or empty passwords should be rated as weak strength");
    }
    
    @ParameterizedTest
    @DisplayName("Test simple passwords should return weak strength")
    @ValueSource(strings = {"1234567", "password", "abcdefgh"})
    public void testSimplePasswordIsWeak(String password) {
        assertEquals(PasswordStrengthChecker.STRENGTH_WEAK, 
                     PasswordStrengthChecker.checkStrength(password),
                     "Simple passwords should be rated as weak strength");
    }
    
    @Test
    @DisplayName("Test passwords with numbers and letters should be fair strength")
    public void testPasswordWithNumbersAndLettersIsFair() {
        assertEquals(PasswordStrengthChecker.STRENGTH_FAIR, 
                     PasswordStrengthChecker.checkStrength("password123"),
                     "Passwords with numbers and letters should be rated as fair strength");
    }
    
    @Test
    @DisplayName("Test passwords with uppercase, lowercase, and numbers should be good strength")
    public void testPasswordWithMixedCaseAndNumbersIsGood() {
        assertEquals(PasswordStrengthChecker.STRENGTH_GOOD, 
                     PasswordStrengthChecker.checkStrength("Password123"),
                     "Passwords with uppercase, lowercase, and numbers should be rated as good strength");
    }
    
    @Test
    @DisplayName("Test passwords with uppercase, lowercase, numbers, and special characters should be very strong")
    public void testPasswordWithMixedCaseNumbersAndSpecialIsStrong() {
        assertEquals(PasswordStrengthChecker.STRENGTH_VERY_STRONG, 
                     PasswordStrengthChecker.checkStrength("Password123!"),
                     "Shorter passwords with uppercase, lowercase, numbers, and special characters should be very strong");
    }
    
    @Test
    @DisplayName("Test long complex passwords should be very strong")
    public void testLongComplexPasswordIsVeryStrong() {
        assertEquals(PasswordStrengthChecker.STRENGTH_VERY_STRONG, 
                     PasswordStrengthChecker.checkStrength("Password123!@#VeryStrong"),
                     "Long passwords with uppercase, lowercase, numbers, and special characters should be very strong");
    }
    
    @ParameterizedTest
    @DisplayName("Test strength level text descriptions")
    @MethodSource("strengthLevelProvider")
    public void testStrengthText(int strengthLevel, String expectedText) {
        assertEquals(expectedText, PasswordStrengthChecker.getStrengthText(strengthLevel),
                    "Strength levels should return correct text descriptions");
    }
    
    private static Stream<Arguments> strengthLevelProvider() {
        return Stream.of(
            Arguments.of(PasswordStrengthChecker.STRENGTH_WEAK, "Weak"),
            Arguments.of(PasswordStrengthChecker.STRENGTH_FAIR, "Fair"),
            Arguments.of(PasswordStrengthChecker.STRENGTH_GOOD, "Good"),
            Arguments.of(PasswordStrengthChecker.STRENGTH_STRONG, "Strong"),
            Arguments.of(PasswordStrengthChecker.STRENGTH_VERY_STRONG, "Very Strong"),
            Arguments.of(-1, "Unknown"),
            Arguments.of(99, "Unknown")
        );
    }
    
    @ParameterizedTest
    @DisplayName("Test whether passwords meet minimum requirements")
    @MethodSource("passwordRequirementsProvider")
    public void testPasswordMeetsMinimumRequirements(String password, boolean expectedResult) {
        assertEquals(expectedResult, 
                     PasswordStrengthChecker.meetsMinimumRequirements(password),
                     "Should correctly determine if password meets minimum requirements");
    }
    
    private static Stream<Arguments> passwordRequirementsProvider() {
        return Stream.of(
            Arguments.of(null, false),
            Arguments.of("", false),
            Arguments.of("abc123", false), // Too short
            Arguments.of("password", false), // No numbers or special characters
            Arguments.of("12345678", false), // No letters or special characters
            Arguments.of("password123", false), // No special characters
            Arguments.of("pass!@#", false), // No numbers and too short
            Arguments.of("password!", false), // No numbers
            Arguments.of("Pass123!", true), // Meets all requirements
            Arguments.of("P@ssw0rd", true) // Meets all requirements
        );
    }
    
    @ParameterizedTest
    @DisplayName("Test getting failed password requirements")
    @MethodSource("failedRequirementsProvider")
    public void testGetFailedRequirements(String password, String[] expectedMessages) {
        String failedRequirements = PasswordStrengthChecker.getFailedRequirements(password);
        
        // Verify that all expected error messages are in the returned string
        for (String message : expectedMessages) {
            assertTrue(failedRequirements.contains(message), 
                      "Failed requirements should include the message: " + message);
        }
    }
    
    private static Stream<Arguments> failedRequirementsProvider() {
        return Stream.of(
            Arguments.of("", new String[]{"Password cannot be empty"}),
            Arguments.of(null, new String[]{"Password cannot be empty"}),
            Arguments.of("short", new String[]{"Password must be at least 8 characters long"}),
            Arguments.of("password", new String[]{"Password must contain at least one number", 
                                               "Password must contain at least one special character"}),
            Arguments.of("12345678", new String[]{"Password must contain at least one letter",
                                               "Password must contain at least one special character"}),
            Arguments.of("Pass123", new String[]{"Password must be at least 8 characters long",
                                              "Password must contain at least one special character"})
        );
    }
} 