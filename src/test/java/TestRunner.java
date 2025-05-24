import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.ClassSelector;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("Starting project unit tests");
        System.out.println("===============================================");
        
        // Create test summary listener
        SummaryGeneratingListener summaryListener = new SummaryGeneratingListener();
        
        // Create logging listener with default log level if null
        Level logLevel = Logger.getLogger(TestRunner.class.getName()).getLevel();
        if (logLevel == null) {
            logLevel = Level.INFO;
        }
        LoggingListener loggingListener = LoggingListener.forJavaUtilLogging(logLevel);
        
        // Create test request, select test package
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage("model"))
            .build();
        
        // Create launcher and add listeners
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(summaryListener, loggingListener);
        
        // Execute tests
        launcher.execute(request);
        
        // Output test summary
        TestExecutionSummary summary = summaryListener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        
        System.out.println("===============================================");
        System.out.println("Unit tests completed");
        System.out.println("===============================================");
        
        // Set exit code if there are failed tests
        if (summary.getTotalFailureCount() > 0) {
            System.exit(1);
        }
    }
} 