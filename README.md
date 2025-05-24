# Financial Management Application

## System Requirements

To use the Financial Management Application, your system should meet the following requirements:
- Operating System: Windows, macOS, or Linux
- Java Runtime Environment (JRE) 8 or higher
- Minimum 4GB RAM
- 500MB free disk space

## Installation and Setup

There are two methods to install and run the application:

### Method 1: Build with Maven, then run the JAR

1. Navigate to the root directory of the project `../groupProject`
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

### Method 2: Run the JAR file directly

1. Navigate to the directory containing the JAR file `../groupProject/target`
2. Run the application using one of these options:
   - Execute the command: `java -jar groupProject-1.0-SNAPSHOT-jar-with-dependencies.jar`
   - Or simply double-click on the `groupProject-1.0-SNAPSHOT-jar-with-dependencies.jar` file

<img src="C:\Users\lyrics61\Pictures\Screenshots\屏幕截图 2025-05-24 104219.png" alt="屏幕截图 2025-05-24 104219" style="zoom:50%;" />

## Running Tests

To run the application tests:

### Method 1: Using Maven

1. Open a terminal or command prompt

2. Navigate to the root directory of the project `../groupProject`

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

1. Navigate to the test directory: `../groupProject/src/test/java`
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

2. Navigate to the test directory: `../groupProject/src/test/java/model`

3. Right-click on a test class (CurrencyManagerTest, PasswordStrengthCheckerTest, etc.)

4. Select "Run 'TestName'"

   <img src="C:\Users\lyrics61\AppData\Roaming\Typora\typora-user-images\image-20250524111337571.png" alt="image-20250524111337571" style="zoom:50%;" />

## JavaDocs Documentation

The application includes comprehensive JavaDocs documentation to help developers understand the code structure and API.

### Accessing JavaDocs

The JavaDocs documentation is available in two locations:

1. **Generated Directory**: 
   - Navigate to `../groupProject/target/site/apidocs`
   - Open `index.html` in a web browser to view the documentation

2. **Main Directory**:
   - Navigate to `../groupProject/javadocs` in the main project directory`../groupProject`
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