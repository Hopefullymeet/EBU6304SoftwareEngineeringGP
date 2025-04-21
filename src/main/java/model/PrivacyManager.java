package model;

import java.util.HashMap;
import java.util.Map;

/**
 * PrivacyManager - 管理应用程序的隐私设置和数据收集选项
 * 根据用户偏好设置控制数据收集和使用
 */
public class PrivacyManager {
    
    // 单例实例
    private static PrivacyManager instance;
    
    // 隐私设置项
    public static final String DATA_ANALYTICS = "data_analytics";
    public static final String ANONYMOUS_DATA = "anonymous_data";
    
    // 隐私设置状态
    private Map<String, Boolean> privacySettings;
    
    /**
     * 私有构造函数（单例模式）
     */
    private PrivacyManager() {
        initializeSettings();
    }
    
    /**
     * 获取PrivacyManager的单例实例
     * @return PrivacyManager实例
     */
    public static PrivacyManager getInstance() {
        if (instance == null) {
            instance = new PrivacyManager();
        }
        return instance;
    }
    
    /**
     * 初始化隐私设置
     */
    private void initializeSettings() {
        privacySettings = new HashMap<>();
        privacySettings.put(DATA_ANALYTICS, true);  // 默认允许数据分析
        privacySettings.put(ANONYMOUS_DATA, true);  // 默认允许匿名数据共享
    }
    
    /**
     * 应用用户隐私设置偏好
     * @param user 当前用户
     */
    public void applyUserPrivacyPreferences(User user) {
        if (user != null) {
            privacySettings.put(DATA_ANALYTICS, user.isAllowDataAnalytics());
            privacySettings.put(ANONYMOUS_DATA, user.isShareAnonymousData());
        }
    }
    
    /**
     * 检查是否允许特定类型的数据收集
     * @param settingKey 设置项键
     * @return 是否允许
     */
    public boolean isDataCollectionAllowed(String settingKey) {
        Boolean allowed = privacySettings.get(settingKey);
        return allowed != null && allowed;
    }
    
    /**
     * 设置数据收集选项
     * @param settingKey 设置项键
     * @param allowed 是否允许
     */
    public void setDataCollectionAllowed(String settingKey, boolean allowed) {
        privacySettings.put(settingKey, allowed);
    }
    
    /**
     * 检查是否允许数据分析
     * @return 是否允许数据分析
     */
    public boolean isDataAnalyticsAllowed() {
        return isDataCollectionAllowed(DATA_ANALYTICS);
    }
    
    /**
     * 检查是否允许共享匿名数据
     * @return 是否允许共享匿名数据
     */
    public boolean isAnonymousDataSharingAllowed() {
        return isDataCollectionAllowed(ANONYMOUS_DATA);
    }
    
    /**
     * 记录用户活动（如果允许数据收集）
     * 此方法会根据隐私设置决定是否记录用户活动
     * @param activityType 活动类型
     * @param details 活动详情
     * @return 是否成功记录
     */
    public boolean logUserActivity(String activityType, Map<String, Object> details) {
        // 检查是否允许数据分析
        if (!isDataAnalyticsAllowed()) {
            return false;
        }
        
        // 这里实现数据分析记录逻辑
        // 在实际应用中，可能会将数据发送到分析服务器或保存到本地数据库
        System.out.println("记录用户活动: " + activityType);
        
        return true;
    }
    
    /**
     * 发送匿名使用数据（如果允许）
     * @param dataType 数据类型
     * @param data 数据内容
     * @return 是否成功发送
     */
    public boolean sendAnonymousData(String dataType, Object data) {
        // 检查是否允许共享匿名数据
        if (!isAnonymousDataSharingAllowed()) {
            return false;
        }
        
        // 这里实现匿名数据发送逻辑
        // 在实际应用中，可能会将数据发送到服务器
        System.out.println("发送匿名数据: " + dataType);
        
        return true;
    }
    
    /**
     * 删除用户收集的所有数据
     * @param username 用户名
     * @return 是否成功删除
     */
    public boolean deleteAllUserData(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        // 这里实现数据删除逻辑
        // 在实际应用中，会删除数据库中的用户数据记录
        System.out.println("删除用户数据: " + username);
        
        return true;
    }
    
    /**
     * 导出用户数据（GDPR合规性）
     * @param username 用户名
     * @return 导出的数据对象，失败则返回null
     */
    public Object exportUserData(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        
        // 这里实现数据导出逻辑
        // 在实际应用中，会从数据库收集用户数据并格式化为可导出的格式
        System.out.println("导出用户数据: " + username);
        
        // 示例返回
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("exportTime", System.currentTimeMillis());
        userData.put("dataType", "Full User Data Export");
        
        return userData;
    }
} 
