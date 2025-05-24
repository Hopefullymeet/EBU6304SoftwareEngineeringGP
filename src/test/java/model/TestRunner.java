package model;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

/**
 * Simple test runner to execute CurrencyManagerTest directly
 */
public class TestRunner {
    
    public static void main(String[] args) {
        // Create a launcher
        Launcher launcher = LauncherFactory.create();
        
        // Create a listener to gather test results
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        
        // Register the listener with the launcher
        launcher.registerTestExecutionListeners(listener);
        
        // Create a request to execute tests in CurrencyManagerTest
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(CurrencyManagerTest.class))
                .build();
        
        // Execute the tests
        launcher.execute(request);
        
        // Print the test summary
        TestExecutionSummary summary = listener.getSummary();
        System.out.println("Tests started: " + summary.getTestsStartedCount());
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        
        // Print details of any failures
        summary.getFailures().forEach(failure -> {
            System.out.println("\nTest failed: " + failure.getTestIdentifier().getDisplayName());
            System.out.println("Reason: " + failure.getException().getMessage());
        });
    }
} 