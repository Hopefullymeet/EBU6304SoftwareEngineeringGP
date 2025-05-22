package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static final String PERSONAL_INFO_SHARING = "personal_info_sharing";
    public static final String LOCATION_TRACKING = "location_tracking";
    public static final String EMAIL_NOTIFICATIONS = "email_notifications";
    public static final String PUSH_NOTIFICATIONS = "push_notifications";
    public static final String THIRD_PARTY_ADS = "third_party_ads";
    public static final String DATA_RETENTION = "data_retention";

    // 隐私设置状态
    private Map<String, Boolean> privacySettings;

    // 存储用户活动中的数值数据，用于计算平均值和峰值
    private List<Double> userActivityValues = new ArrayList<>();

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
        privacySettings.put(PERSONAL_INFO_SHARING, false);  // 默认不共享个人信息
        privacySettings.put(LOCATION_TRACKING, false);  // 默认不跟踪位置
        privacySettings.put(EMAIL_NOTIFICATIONS, true);  // 默认允许邮件通知
        privacySettings.put(PUSH_NOTIFICATIONS, false);  // 默认不允许推送通知
        privacySettings.put(THIRD_PARTY_ADS, false);  // 默认不允许第三方广告
        privacySettings.put(DATA_RETENTION, true);  // 默认允许数据保留
    }

    /**
     * 应用用户隐私设置偏好
     * @param user 当前用户
     */
    public void applyUserPrivacyPreferences(User user) {
        if (user != null) {
            privacySettings.put(DATA_ANALYTICS, user.isAllowDataAnalytics());
            privacySettings.put(ANONYMOUS_DATA, user.isShareAnonymousData());
            privacySettings.put(PERSONAL_INFO_SHARING, user.isAllowPersonalInfoSharing());
            privacySettings.put(LOCATION_TRACKING, user.isAllowLocationTracking());
            privacySettings.put(EMAIL_NOTIFICATIONS, user.isAllowEmailNotifications());
            privacySettings.put(PUSH_NOTIFICATIONS, user.isAllowPushNotifications());
            privacySettings.put(THIRD_PARTY_ADS, user.isAllowThirdPartyAds());
            privacySettings.put(DATA_RETENTION, user.isAllowDataRetention());
        }
    }

    /**
     * 检查是否允许数据分析
     * @return 是否允许数据分析
     */
    public boolean isDataAnalyticsAllowed() {
        return isDataCollectionAllowed(DATA_ANALYTICS);
    }

    /**
     * 设置是否允许数据分析
     * @param allowed 是否允许
     */
    public void setDataAnalyticsAllowed(boolean allowed) {
        setDataCollectionAllowed(DATA_ANALYTICS, allowed);
    }

    /**
     * 检查是否允许共享匿名数据
     * @return 是否允许共享匿名数据
     */
    public boolean isAnonymousDataSharingAllowed() {
        return isDataCollectionAllowed(ANONYMOUS_DATA);
    }

    /**
     * 设置是否允许共享匿名数据
     * @param allowed 是否允许
     */
    public void setAnonymousDataSharingAllowed(boolean allowed) {
        setDataCollectionAllowed(ANONYMOUS_DATA, allowed);
    }

    /**
     * 检查是否允许共享个人信息
     * @return 是否允许共享个人信息
     */
    public boolean isPersonalInfoSharingAllowed() {
        return isDataCollectionAllowed(PERSONAL_INFO_SHARING);
    }

    /**
     * 设置是否允许共享个人信息
     * @param allowed 是否允许
     */
    public void setPersonalInfoSharingAllowed(boolean allowed) {
        setDataCollectionAllowed(PERSONAL_INFO_SHARING, allowed);
    }

    /**
     * 检查是否允许位置跟踪
     * @return 是否允许位置跟踪
     */
    public boolean isLocationTrackingAllowed() {
        return isDataCollectionAllowed(LOCATION_TRACKING);
    }

    /**
     * 设置是否允许位置跟踪
     * @param allowed 是否允许
     */
    public void setLocationTrackingAllowed(boolean allowed) {
        setDataCollectionAllowed(LOCATION_TRACKING, allowed);
    }

    /**
     * 检查是否允许邮件通知
     * @return 是否允许邮件通知
     */
    public boolean isEmailNotificationsAllowed() {
        return isDataCollectionAllowed(EMAIL_NOTIFICATIONS);
    }

    /**
     * 设置是否允许邮件通知
     * @param allowed 是否允许
     */
    public void setEmailNotificationsAllowed(boolean allowed) {
        setDataCollectionAllowed(EMAIL_NOTIFICATIONS, allowed);
    }

    /**
     * 检查是否允许推送通知
     * @return 是否允许推送通知
     */
public boolean isPushNotificationsAllowed() {
        return isDataCollectionAllowed(PUSH_NOTIFICATIONS);
    }

    /**
     * 设置是否允许推送通知
     * @param allowed 是否允许
     */
    public void setPushNotificationsAllowed(boolean allowed) {
        setDataCollectionAllowed(PUSH_NOTIFICATIONS, allowed);
    }

    /**
     * 检查是否允许第三方广告
     * @return 是否允许第三方广告
     */
    public boolean isThirdPartyAdsAllowed() {
        return isDataCollectionAllowed(THIRD_PARTY_ADS);
    }

    /**
     * 设置是否允许第三方广告
     * @param allowed 是否允许
     */
    public void setThirdPartyAdsAllowed(boolean allowed) {
        setDataCollectionAllowed(THIRD_PARTY_ADS, allowed);
    }

    /**
     * 检查是否允许数据保留
     * @return 是否允许数据保留
     */
    public boolean isDataRetentionAllowed() {
        return isDataCollectionAllowed(DATA_RETENTION);
    }

    /**
     * 设置是否允许数据保留
     * @param allowed 是否允许
     */
    public void setDataRetentionAllowed(boolean allowed) {
        setDataCollectionAllowed(DATA_RETENTION, allowed);
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

    // 尝试从详情中提取数值数据
    if (details != null && details.containsKey("value")) {
        Object valueObj = details.get("value");
        if (valueObj instanceof Number) {
            double value = ((Number) valueObj).doubleValue();
            userActivityValues.add(value);
        }
    }

    try (FileWriter writer = new FileWriter("user_activity.log", true)) {
        writer.write("活动类型: " + activityType + ", 详情: " + details + "\n");
        if (!userActivityValues.isEmpty()) {
            double average = calculateAverage();
            double peak = calculatePeak();
            writer.write("数据分析 - 平均值: " + average + ", 峰值: " + peak + "\n");
        }
        return true;
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
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

        try (FileWriter writer = new FileWriter("anonymous_data.log", true)) {
            writer.write("数据类型: " + dataType + ", 数据内容: " + data + "\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

        // 清空存储的用户活动数值数据
        userActivityValues.clear();

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

        System.out.println("导出用户数据: " + username);

        // 示例返回
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("exportTime", System.currentTimeMillis());
        userData.put("dataType", "Full User Data Export");

        return userData;
    }

    /**
     * 计算用户活动数值数据的平均值
     * @return 平均值
     */
    private double calculateAverage() {
        if (userActivityValues.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (double value : userActivityValues) {
            sum += value;
        }
        return sum / userActivityValues.size();
    }

    /**
     * 计算用户活动数值数据的峰值
     * @return 峰值
     */
    private double calculatePeak() {
        if (userActivityValues.isEmpty()) {
            return 0;
        }
        double peak = userActivityValues.get(0);
        for (double value : userActivityValues) {
            if (value > peak) {
                peak = value;
            }
        }
        return peak;
    }
}
