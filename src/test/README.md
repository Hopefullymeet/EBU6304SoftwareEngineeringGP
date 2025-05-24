# Financial Management Application - Unit Tests

This document describes how to run and understand the project's unit tests. We have adopted a Test-Driven Development (TDD) approach and written comprehensive test cases for the core model classes.

## Test Structure

Tests are organized in the `src/test/java` directory, corresponding to the source code package structure:

```
src/test/java/
├── model/
│   ├── TransactionTest.java         // Transaction class tests
│   ├── UserTest.java                // User class tests
│   ├── PasswordStrengthCheckerTest.java  // Password strength checker tests
│   └── CurrencyManagerTest.java     // Currency manager tests
└── TestRunner.java                  // Test runner
```

## How to Run Tests

There are two ways to run the tests:

### 1. Using Maven

Run the following command in the project root directory:

```bash
mvn test
```

### 2. Using IDE

If you are using an IDE such as Eclipse or IntelliJ IDEA, you can:

- Right-click on a specific test class and select "Run as JUnit Test"
- Right-click on the `src/test/java` directory and select "Run Tests in..."
- Run the `TestRunner.java` file, which will execute all tests and generate a summary

## Test Suite Description

### TransactionTest

Tests the creation, conversion, and related functionality of the Transaction class:
- Validates constructors (with and without user ID)
- Tests the toString method
- Tests creating Transaction objects from strings
- Tests setter methods

### UserTest

Tests the core functionality of the User class:
- Validates constructors and default values
- Tests setter methods
- Tests toFileString and fromFileString methods

### PasswordStrengthCheckerTest

Tests the various functions of the password strength checker:
- Tests passwords of different strength levels
- Validates password strength text representation
- Tests whether passwords meet minimum requirements
- Tests getting the unmet password requirements

### CurrencyManagerTest

Tests currency conversion and formatting functionality:
- Tests the getInstance method and singleton pattern
- Tests conversion between the same currencies
- Tests conversion between different currencies
- Tests currency formatting
- Tests getting currency symbols
- Tests exchange rate updates
- Tests getting exchange rates

## Writing New Tests

When adding new features or modifying existing ones, the corresponding tests should be updated or added. Follow these steps:

1. Create a new test method in the appropriate test class
2. Mark the test method with the `@Test` annotation
3. Optionally add a `@DisplayName` annotation to provide a human-readable description
4. Follow the "Arrange-Act-Assert" pattern:
   - Prepare test data
   - Execute the method being tested
   - Verify that the results match expectations

## Reference Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Introduction to Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html) 