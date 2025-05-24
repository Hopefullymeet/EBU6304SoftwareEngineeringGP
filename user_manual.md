# Financial Management Application - User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
   - [System Requirements](#system-requirements)
   - [Installation](#installation)
   - [Registration](#registration)
   - [Login](#login)
3. [Main Navigation](#main-navigation)
4. [Account](#account)
   - [Profile](#profile)
   - [Accounts Manager](#accounts-manager)
   - [Preferences](#preferences)
5. [Billing & Subscriptions](#billing--subscriptions)
   - [Overview](#overview)
   - [Manual Entry](#manual-entry)
   - [Import Transactions](#import-transactions)
6. [Budget](#budget)
   - [Budget Overview](#budget-overview)
   - [AI Insights](#ai-insights)
   - [Budget Settings](#budget-settings)
7. [Help Center](#help-center)
   - [FAQ](#faq)
   - [Live Support](#live-support)
   - [Feedback](#feedback)
8. [Financial Advisor](#financial-advisor)
   - [Connecting to the AI Advisor](#connecting-to-the-ai-advisor)
10. [Running Tests](#running-tests)
11. [JavaDocs Documentation](#javadocs-documentation)

## Introduction

Welcome to the Financial Management Application, a comprehensive personal finance tool designed to help you track expenses, manage budgets, and gain insights into your financial health. This user manual will guide you through the various features and functionalities of the application.

The application includes the following key features:
- Secure user authentication and account management
- Transaction tracking and categorization
- Budget creation and monitoring
- Financial insights and reports
- Multi-currency support
- Bill management and reminders
- AI-powered financial advice

![Application home page showing main dashboard]

## Getting Started

### System Requirements

To use the Financial Management Application, your system should meet the following requirements:
- Operating System: Windows, macOS, or Linux
- Java Runtime Environment (JRE) 8 or higher
- Minimum 4GB RAM
- 500MB free disk space

### Installation

There are two methods to install and run the application:

**Method 1: Build with Maven, then run the JAR**
1. Navigate to the root directory of the project `..\groupProject`
2. Build the project using Maven:
   ```
   mvn clean package
   ```
3. Navigate to the target directory:
   ```
   cd target
   ```
4. Run the generated JAR file:
   ```
   java -jar groupProject-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104056.png" alt="屏幕截图 2025-05-24 104056" style="zoom:50%;" />

**Method 2: Run the JAR file directly**

1. Navigate to the directory containing the JAR file  `..\groupProject\target`
2. Run the application using one of these options:
   - Execute the command: `java -jar groupProject-1.0-SNAPSHOT-jar-with-dependencies.jar`
   - Or simply double-click on the `groupProject-1.0-SNAPSHOT-jar-with-dependencies.jar` file

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104219.png" alt="屏幕截图 2025-05-24 104219" style="zoom:50%;" />

### Registration

When you first open the application, you'll be prompted to create an account:

1. Click on the "Register" button on the login screen
2. Complete the registration form with your details:
   - Username (must be unique)
   - Email address (used for account recovery)
   - Password (at least 8 characters with a combination of letters, numbers, and special characters)
3. Review and accept the terms and conditions
4. Click "Create Account"

The application will check the strength of your password and provide feedback. For security reasons, we recommend using a strong password.

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104319.png" alt="屏幕截图 2025-05-24 104319" style="zoom: 50%;" />

### Login

Once registered, you can log in to your account:

1. Enter your username or email address
2. Enter your password
3. Click "Login"
4. Optional: Check "Remember me" to stay logged in on your personal device

If you forget your password, click on the "Forgot Password" link to reset it via email.

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 105208.png" alt="屏幕截图 2025-05-24 105208" style="zoom:50%;" />

## Main Navigation

The application features a sidebar navigation menu with five main sections:

1. **Account** - Manage your personal information and application settings
2. **Billing & Subscriptions** - Track transactions and manage your financial data
3. **Budget** - Set and monitor spending limits
4. **Help Center** - Access help documentation and support resources
5. **Financial Advisor** - Receive AI-powered financial insights and recommendations

Each main section may contain subsections that can be accessed by clicking on the main section name to expand or collapse the menu.

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524113422972.png" alt="image-20250524113422972" style="zoom:50%;" />

## Account

The Account section provides comprehensive tools to manage your personal information, security settings, and preferences. Navigate between different account areas using the sidebar on the left side.

### Profile

The Profile page displays your personal information and security settings:

1. Access the Profile page by:
   - Clicking "Account" in the sidebar
   - Then selecting "Profile" from the expanded menu

2. Personal Information section includes:
   - Name
   - Email address
   - Phone number
   
3. Security Settings section includes:
   - Password management (Change Password button)
   - Session timeout settings (1-60 minutes)
   - Auto-logout after inactivity feature

4. Click "Save Changes" to update your settings

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104857.png" alt="屏幕截图 2025-05-24 104857" style="zoom:50%;" />

### Accounts Manager

The Accounts Manager allows you to manage multiple user accounts and view budget information:

1. Access the Accounts Manager by:
   - Clicking "Account" in the sidebar
   - Then selecting "Accounts Manager" from the expanded menu

2. The top section shows budget information:
   - Remaining Budget with prominent display (e.g., ¥4,841.75)
   - Total Budget (e.g., ¥5,000.00)
   - Amount Spent (e.g., ¥158.25)
   - Remaining Balance (e.g., ¥4,841.75)

3. User Accounts section lists all registered users:
   - Each user card shows username, email, and budget balance
   - Current account is highlighted
   - "Switch Account" buttons let you log in as a different user
   - "Register New User" button to add new accounts

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104916.png" alt="屏幕截图 2025-05-24 104916" style="zoom:50%;" />

### Preferences

The Preferences page lets you customize application display and behavior:

1. Access Preferences by:
   - Clicking "Account" in the sidebar
   - Then selecting "Preferences" from the expanded menu

2. Currency Settings:
   - Select your preferred display currency (CNY, USD, EUR)
   - Preview shows conversion examples (e.g., 1,000.00 CNY equals ¥1,000.00)
   - All amounts will be displayed in your selected currency

3. Chinese New Year Personalization:
   - Red & Yellow Theme option (affects all pages)
   - Budget Boost option to increase budget to 10,000 (Budget Page)

4. Click "Save Preferences" to apply your changes

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104926.png" alt="屏幕截图 2025-05-24 104926" style="zoom:50%;" />

## Billing & Subscriptions

The Billing & Data Entry section helps you manage your financial transactions and subscription information. Navigate between the different billing areas using the sidebar menu.

### Overview

The Overview page displays your subscription details and recent transactions:

1. Access the Overview page by:
   - Clicking "Billing & Subscriptions" in the sidebar
   - Then selecting "Overview" from the expanded menu

2. Subscription Information:
   - Current plan status (e.g., "Finance Tracker Pro")
   - Active status and next billing date
   - Subscription features and benefits

3. Recent Transactions:
   - List of your most recently recorded transactions
   - Each transaction shows date, description, category, and amount
   - Transactions are color-coded (green for income, red for expenses)

4. Transaction Data Entry Options:
   - Manual Entry: Add individual transactions manually
   - Import Transactions: Import CSV files from your bank

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524110750255.png" alt="image-20250524110750255" style="zoom:50%;" />

### Manual Entry

The Manual Entry page allows you to add individual transactions with AI-assisted categorization:

1. Access Manual Entry by:
   - Clicking "Billing & Subscriptions" in the sidebar
   - Then selecting "Manual Entry" from the expanded menu

2. Transaction Form Fields:
   - Date (yyyy-MM-dd format)
   - Description (details about the transaction)
   - Expense Amount (in your preferred currency)
   - Category (with AI categorization option)

3. AI Assistant Features:
   - Intelligent categorization based on transaction description
   - Enter a description and select "AI Categorize" from the category dropdown
   - The AI will analyze the description and suggest an appropriate category

4. Click "Add Transaction" to save the entry to your financial records

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524110906546.png" alt="image-20250524110906546" style="zoom:50%;" />

### Import Transactions

The Import Transactions page lets you import transaction data from CSV files:

1. Access Import Transactions by:
   - Clicking "Billing & Subscriptions" in the sidebar
   - Then selecting "Import Transactions" from the expanded menu

2. File Selection:
   - Click "Browse" to select a CSV file from your computer
   - "Download Sample CSV" provides a template in the correct format

3. Required CSV Format:
   - Required columns: Date, Description, Category, Amount
   - Supports various date formats (yyyy-MM-dd, yyyy/MM/dd, etc.)
   - System will automatically detect file format and column headers

4. AI Categorization Settings:
   - Automatically categorize transactions using AI
   - Review transactions before saving (Recommended)
   - Detect seasonal spending patterns

5. Click "Import Transactions" to process the file and add transactions to your financial records

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524110918225.png" alt="image-20250524110918225" style="zoom:50%;" />

## Budget

The Budget section helps you manage your spending by setting limits and tracking expenses. Navigate between different budget areas using the sidebar menu.

### Budget Overview

The Budget Overview page provides a comprehensive summary of your financial situation:

1. Access the Budget Overview page by:
   - Clicking "Budget" in the sidebar
   - Then selecting "Budget Overview" from the expanded menu

2. AI Budget Insights panel:
   - Quick summary of your current spending status
   - Shows percent of monthly budget used (e.g., "You've spent 3.8% of your monthly budget")
   - Displays remaining budget amount (e.g., "¥4809.75 remaining")
   - Alert notifications with personalized recommendations

3. Budget Summary section:
   - Total Budget: Shows your monthly budget amount (e.g., "¥5000.00")
   - Spent: Displays how much you've spent this month (e.g., "¥190.25")
   - Remaining: Shows remaining funds (e.g., "¥4809.75")

4. Category Breakdown:
   - Lists spending by category (e.g., Food)
   - Shows progress bars for each category
   - Displays amount spent and budget allocated per category

5. Budget Distribution:
   - Pie chart visualization of your spending
   - Different colors for each spending category
   - Visual representation of remaining budget

6. Recent Transactions:
   - Table of your latest expenses
   - Shows date, description, category, and amount
   - Provides quick overview of recent spending activity

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112520008.png" alt="image-20250524112520008" style="zoom:50%;" />

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112527870.png" alt="image-20250524112527870" style="zoom:50%;" />

### AI Insights

The AI Insights page provides personalized financial recommendations powered by artificial intelligence:

1. Access AI Insights by:
   - Clicking "Budget" in the sidebar
   - Then selecting "AI Insights" from the expanded menu

2. Monthly Budget Summary:
   - Shows current budget, spent amount, and remaining balance at the top
   - Format: "Monthly Budget: ¥5000.00 | Spent: ¥190.25 | Remaining: ¥4809.75"

3. Budget Insights Generator:
   - Click "Generate" button to create personalized financial recommendations
   - AI analyzes your spending patterns and financial behavior
   - Generates tailored suggestions to improve your financial health

4. Insights Display Area:
   - Shows AI-generated recommendations after clicking Generate
   - Bullet points with actionable financial advice
   - Highlights areas where you can save money or improve spending habits

5. Data Controls:
   - "Refresh Data" button to update with latest transaction information
   - "Auto-refresh when data changes" checkbox to keep insights current
   - Ensures insights are based on your most recent financial activity

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112550751.png" alt="image-20250524112550751" style="zoom:50%;" />

### Budget Settings

The Budget Settings page allows you to customize your budget configuration:

1. Access Budget Settings by:
   - Clicking "Budget" in the sidebar
   - Then selecting "Budget Settings" from the expanded menu

2. Monthly Budget Configuration:
   - Set your total monthly budget amount (e.g., "5000.00")
   - Input field allows you to enter custom budget amounts
   - Changes affect budget tracking throughout the application

3. Transaction Settings:
   - "Auto-categorize transactions" checkbox to enable AI categorization
   - System automatically assigns categories to new transactions when enabled
   - Saves time when entering new expenses

4. Notification Settings:
   - "Budget alerts" checkbox to enable notification system
   - Receive alerts when approaching or exceeding budget limits
   - Helps prevent overspending

5. Save your changes with the "Save Settings" button to apply new configuration

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112608656.png" alt="image-20250524112608656" style="zoom:50%;" />

## Help Center

The Help Center provides resources and support to help you use the application effectively. Navigate between different support areas using the sidebar menu.

### FAQ

The FAQ page offers answers to commonly asked questions about the application:

1. Access the FAQ page by:
   - Clicking "Help Center" in the sidebar
   - Then selecting "FAQ" from the expanded menu

2. FAQ List Features:
   - Organized list of frequently asked questions
   - Click on any question to view the detailed answer
   - Covers topics like connecting bank accounts, AI functionality, data security, and budget alerts

3. Interactive Question Format:
   - Each question has an arrow indicator for expanding
   - Clicking a question opens a detailed answer dialog
   - Answers include step-by-step instructions and explanations

4. Common FAQ Topics:
   - "How do I connect my bank accounts?"
   - "How does the AI analyze my spending habits?"
   - "Is my financial data secure?"
   - "How can I set up budget alerts?"

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112915564.png" alt="image-20250524112915564" style="zoom:50%;" />

### Live Support

The Live Support page connects you with customer service representatives:

1. Access Live Support by:
   - Clicking "Help Center" in the sidebar
   - Then selecting "Live Support" from the expanded menu

2. Support Services:
   - Real-time chat assistance with support team
   - Help with AI-powered finance tracker questions
   - Technical support for application issues

3. Available Hours:
   - Monday to Friday, 9:00 - 18:00
   - Click "Start Chat" button to begin a support session

4. Alternative Contact Methods:
   - Email: nzy1522512646@gmail.com<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112941888.png" alt="image-20250524112941888" style="zoom:50%;" />

### Feedback

The Feedback page allows you to submit comments, suggestions, or report issues:

1. Access the Feedback form by:
   - Clicking "Help Center" in the sidebar
   - Then selecting "Feedback" from the expanded menu

2. Form Fields:
   - Feedback Type: Select from dropdown (Bug Report, Feature Request, General Feedback, Other)
   - Subject: Brief description of your feedback topic
   - Message: Detailed explanation of your feedback or issue
   - Contact Info: Your email or phone number for follow-up

3. Submission Process:
   - Complete all relevant fields
   - Click the "Submit Feedback" button
   - Your feedback will be reviewed by our team

4. Response Time:
   - Bug reports are typically addressed within 48 hours
   - Feature requests are collected for future updates
   - General feedback is used to improve the application

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524112951277.png" alt="image-20250524112951277" style="zoom:50%;" />

## Financial Advisor

The Financial Advisor section provides AI-powered financial guidance through a chat interface. This feature allows you to ask questions and receive personalized advice about various financial topics.

### Connecting to the AI Advisor

1. Access the Financial Advisor by:
   - Clicking "Financial Advisor" in the main sidebar

2. Configuration Options:
   - DeepSeek API Key: Enter your personal API key, or leave blank to use the default key
   - Model Selection: Choose between two AI models:
     - DeepSeek-Chat (V3): General-purpose chat model with broad knowledge
     - DeepSeek-Reasoner (R1): Better for logical reasoning and complex financial analysis
   - Click "Connect" to establish connection to the AI service
   - Connection status is displayed below the button

3. Using the Chat Interface:
   - Type your financial questions in the input field at the bottom
   - Click "Send" or press Enter to submit your question
   - AI responses appear in the main chat area
   - Conversations are displayed chronologically with timestamps

4. Available Actions:
   - Clear Chat: Removes all previous conversations and starts fresh
   - Back button (←): Returns to the previous screen
   - Logout: Exits the application and returns to login screen

5. Topic Suggestions:
   - Quick access to common financial topics
   - Click any suggestion button to automatically send that query:
     - Saving for retirement
     - Investment strategies
     - Budgeting tips
     - Debt management
     - Tax planning

The Financial Advisor can help with budgeting advice, investment recommendations, retirement planning, tax strategies, debt management, and other financial decisions based on your personal situation.

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524113450303.png" alt="image-20250524113450303" style="zoom: 33%;" />

## Running Tests

To run the application tests:

### Method 1: Using Maven
1. Open a terminal or command prompt

2. Navigate to the root directory of the project

3. Run the following command:
   ```
   mvn test
   ```
   This will execute all tests in the project
   
   <img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524111034538.png" alt="image-20250524111034538" style="zoom: 50%;" />
   
   <img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524111049640.png" alt="image-20250524111049640" style="zoom:50%;" />

**Note:** If Maven reports "Tests run: 0" even though you have test files, make sure:
- You're using JUnit 5 annotations (@Test, @DisplayName, etc.)
- Surefire plugin is properly configured in pom.xml
- You may need to explicitly add the Surefire plugin to your pom.xml:
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <version>3.0.0-M5</version>
   </plugin>
   ```

### Method 2: Using the TestRunner class
1. Navigate to the test directory: `src/test/java`
2. Run the TestRunner class directly:
   ```
   java -cp <classpath> TestRunner
   ```
   Or run the specific TestRunner in the model package:
   ```
   java -cp <classpath> model.TestRunner
   ```

### Method 3: Using IDE (IntelliJ IDEA)
1. Open the project in IntelliJ IDEA
2. Navigate to the test directory: `src/test/java/model`
3. Right-click on a test class (CurrencyManagerTest, PasswordStrengthCheckerTest, etc.)
4. Select "Run 'TestName'"

Tests verify critical functionality of the application:
- CurrencyManagerTest: Tests currency conversion and formatting
- PasswordStrengthCheckerTest: Tests password validation rules
- TransactionTest: Tests transaction creation and management
- UserTest: Tests user account functionality

Test results will show passed, failed, and skipped tests along with any error messages.

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524111337571.png" alt="image-20250524111337571" style="zoom:50%;" />

## JavaDocs Documentation

The application includes comprehensive JavaDocs documentation to help developers understand the code structure and API.

### Accessing JavaDocs

The JavaDocs documentation is available in two locations:

1. **Generated Directory**: 
   - Navigate to `target/site/apidocs`
   - Open `index.html` in a web browser to view the documentation

2. **Main Directory**:
   - Navigate to `/javadocs` in the main project directory
   - Open `index.html` in a web browser

### Using JavaDocs

The JavaDocs documentation provides:
- Complete class descriptions
- Method parameters and return values
- Inheritance hierarchies
- Package structures
- Examples of usage

This documentation is particularly useful for understanding the internal workings of the application and for developers who want to extend or maintain the codebase.

<img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524111257354.png" alt="image-20250524111257354" style="zoom:50%;" />