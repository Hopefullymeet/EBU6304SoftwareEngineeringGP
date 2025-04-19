package model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Date;

/**
 * SessionManager - Manages user session timeouts.
 * Tracks user activity and logs out after a period of inactivity.
 */
public class SessionManager {
    
    private static SessionManager instance;
    private Timer sessionTimer;
    private Date lastActivity;
    private JFrame activeFrame;
    private boolean isTimerRunning = false;
    
    /**
     * Private constructor for singleton pattern
     */
    private SessionManager() {
        lastActivity = new Date();
    }
    
    /**
     * Gets the singleton instance of SessionManager
     * @return The SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Starts session monitoring
     * @param frame The active JFrame to monitor
     */
    public void startSession(JFrame frame) {
        this.activeFrame = frame;
        lastActivity = new Date();
        
        if (sessionTimer != null) {
            sessionTimer.stop();
        }
        
        // Get timeout from current user
        int timeoutMinutes = 1; // Default timeout
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            timeoutMinutes = currentUser.getSessionTimeoutMinutes();
        }
        
        // Create and start session timer
        int delay = timeoutMinutes * 60 * 1000; // Convert minutes to milliseconds
        sessionTimer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSessionTimeout();
            }
        });
        sessionTimer.start();
        isTimerRunning = true;
        
        // Add activity listener to frame
        addActivityListener(frame);
    }
    
    /**
     * Stops session monitoring
     */
    public void stopSession() {
        if (sessionTimer != null) {
            sessionTimer.stop();
            isTimerRunning = false;
        }
    }
    
    /**
     * Adds a mouse motion listener to track user activity
     * @param frame The JFrame to monitor
     */
    private void addActivityListener(JFrame frame) {
        MouseMotionListener activityListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateActivity();
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                updateActivity();
            }
        };
        
        // Add listener to frame and all its components
        frame.addMouseMotionListener(activityListener);
        
        // Also refresh timer when any key is pressed
        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                updateActivity();
            }
        });
    }
    
    /**
     * Updates the last activity timestamp
     */
    public void updateActivity() {
        lastActivity = new Date();
    }
    
    /**
     * Checks if the session should timeout
     */
    private void checkSessionTimeout() {
        // If timer isn't running, don't check
        if (!isTimerRunning) {
            return;
        }
        
        Date now = new Date();
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            stopSession();
            return;
        }
        
        // Calculate elapsed time in milliseconds since last activity
        long elapsed = now.getTime() - lastActivity.getTime();
        long timeoutMillis = currentUser.getSessionTimeoutMinutes() * 60 * 1000;
        
        if (elapsed > timeoutMillis) {
            sessionTimeout();
        }
    }
    
    /**
     * Handles session timeout by showing a warning and logging out
     */
    private void sessionTimeout() {
        stopSession();
        
        // Show warning message
        JOptionPane.showMessageDialog(
            activeFrame,
            "Your session has timed out due to inactivity.",
            "Session Timeout",
            JOptionPane.WARNING_MESSAGE
        );
        
        // Logout user
        UserManager.getInstance().logout();
        
        // Close current frame and open login
        if (activeFrame != null) {
            activeFrame.dispose();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new view.LoginView();
                }
            });
        }
    }
} 