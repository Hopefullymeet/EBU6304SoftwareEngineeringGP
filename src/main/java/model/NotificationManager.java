package model;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NotificationManager - 管理应用程序的通知系统
 * 根据用户偏好设置提供不同类型的通知
 */
public class NotificationManager {
    
    // 单例实例
    private static NotificationManager instance;
    
    // 通知类型
    public static final String TRANSACTION_ALERT = "transaction";
    public static final String BUDGET_ALERT = "budget";
    public static final String BILL_REMINDER = "bill";
    public static final String FINANCIAL_TIP = "tip";
    
    // 用户通知偏好 (默认全部开启)
    private Map<String, Boolean> notificationPreferences;
    
    // 通知历史记录
    private List<Notification> notificationHistory;
    
    // 待处理的通知
    private List<Notification> pendingNotifications;
    
    /**
     * 通知内部类
     */
    public static class Notification {
        public String type;
        public String title;
        public String message;
        public long timestamp;
        public boolean isRead;
        
        public Notification(String type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
            this.isRead = false;
        }
    }
    
    /**
     * 私有构造函数（单例模式）
     */
    private NotificationManager() {
        initializePreferences();
        notificationHistory = new ArrayList<>();
        pendingNotifications = new ArrayList<>();
    }
    
    /**
     * 获取NotificationManager的单例实例
     * @return NotificationManager实例
     */
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    /**
     * 初始化通知偏好
     */
    private void initializePreferences() {
        notificationPreferences = new HashMap<>();
        notificationPreferences.put(TRANSACTION_ALERT, true);
        notificationPreferences.put(BUDGET_ALERT, true);
        notificationPreferences.put(BILL_REMINDER, true);
        notificationPreferences.put(FINANCIAL_TIP, true);
    }
    
    /**
     * 应用用户通知偏好
     * @param user 当前用户
     */
    public void applyUserNotificationPreferences(User user) {
        if (user != null) {
            notificationPreferences.put(TRANSACTION_ALERT, user.isTransactionAlerts());
            notificationPreferences.put(BUDGET_ALERT, user.isBudgetAlerts());
            notificationPreferences.put(BILL_REMINDER, user.isBillReminders());
            notificationPreferences.put(FINANCIAL_TIP, user.isFinancialTips());
        }
    }
    
    /**
     * 发送通知
     * @param type 通知类型
     * @param title 通知标题
     * @param message 通知内容
     * @return 是否成功发送
     */
    public boolean sendNotification(String type, String title, String message) {
        // 检查通知类型是否启用
        Boolean enabled = notificationPreferences.get(type);
        if (enabled == null || !enabled) {
            return false;
        }
        
        // 创建新通知
        Notification notification = new Notification(type, title, message);
        
        // 添加到历史记录和待处理列表
        notificationHistory.add(notification);
        pendingNotifications.add(notification);
        
        // 如果当前正在运行UI，则显示通知
        showNotification(notification);
        
        return true;
    }
    
    /**
     * 显示通知
     * @param notification 要显示的通知
     */
    private void showNotification(Notification notification) {
        // 创建通知UI
        SwingUtilities.invokeLater(() -> {
            try {
                // 创建通知窗口
                JDialog notificationDialog = new JDialog();
                notificationDialog.setUndecorated(true);
                notificationDialog.setAlwaysOnTop(true);
                notificationDialog.setSize(300, 100);
                
                // 放置在屏幕右下角
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                notificationDialog.setLocation(
                    screenSize.width - notificationDialog.getWidth() - 20,
                    screenSize.height - notificationDialog.getHeight() - 20
                );
                
                // 通知内容面板
                JPanel contentPanel = new JPanel(new BorderLayout());
                contentPanel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
                contentPanel.setBackground(Color.WHITE);
                
                // 标题和关闭按钮
                JPanel titlePanel = new JPanel(new BorderLayout());
                titlePanel.setBackground(new Color(52, 152, 219));
                
                JLabel titleLabel = new JLabel(notification.title);
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                JButton closeButton = new JButton("×");
                closeButton.setForeground(Color.WHITE);
                closeButton.setBackground(new Color(52, 152, 219));
                closeButton.setBorderPainted(false);
                closeButton.setFocusPainted(false);
                closeButton.addActionListener(e -> {
                    notificationDialog.dispose();
                    notification.isRead = true;
                    pendingNotifications.remove(notification);
                });
                
                titlePanel.add(titleLabel, BorderLayout.CENTER);
                titlePanel.add(closeButton, BorderLayout.EAST);
                
                // 消息内容
                JLabel messageLabel = new JLabel("<html>" + notification.message + "</html>");
                messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                contentPanel.add(titlePanel, BorderLayout.NORTH);
                contentPanel.add(messageLabel, BorderLayout.CENTER);
                
                notificationDialog.add(contentPanel);
                notificationDialog.setVisible(true);
                
                // 自动关闭定时器（5秒后）
                Timer timer = new Timer(5000, e -> {
                    notificationDialog.dispose();
                    notification.isRead = true;
                    pendingNotifications.remove(notification);
                });
                timer.setRepeats(false);
                timer.start();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 获取通知历史记录
     * @return 通知历史列表
     */
    public List<Notification> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
    
    /**
     * 获取待处理通知
     * @return 待处理通知列表
     */
    public List<Notification> getPendingNotifications() {
        return new ArrayList<>(pendingNotifications);
    }
    
    /**
     * 检查特定类型的通知是否启用
     * @param type 通知类型
     * @return 是否启用
     */
    public boolean isNotificationEnabled(String type) {
        Boolean enabled = notificationPreferences.get(type);
        return enabled != null && enabled;
    }
    
    /**
     * 设置通知状态
     * @param type 通知类型
     * @param enabled 是否启用
     */
    public void setNotificationEnabled(String type, boolean enabled) {
        notificationPreferences.put(type, enabled);
    }
    
    /**
     * 清空通知历史
     */
    public void clearNotificationHistory() {
        notificationHistory.clear();
    }
    
    /**
     * 将所有待处理通知标记为已读
     */
    public void markAllNotificationsAsRead() {
        for (Notification notification : pendingNotifications) {
            notification.isRead = true;
        }
        pendingNotifications.clear();
    }
} 