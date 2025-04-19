package model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * LocalizationManager - 管理应用程序的多语言支持
 * 提供不同语言的文本和资源
 */
public class LocalizationManager {
    
    // 单例实例
    private static LocalizationManager instance;
    
    // 当前语言
    private String currentLanguage = "English";
    
    // 语言到Locale的映射
    private Map<String, Locale> locales;
    
    // 当前资源包
    private ResourceBundle resourceBundle;
    
    // 资源包基础名称
    private static final String BUNDLE_NAME = "i18n.messages";
    
    /**
     * 私有构造函数（单例模式）
     */
    private LocalizationManager() {
        initializeLocales();
        loadResourceBundle();
    }
    
    /**
     * 获取LocalizationManager的单例实例
     * @return LocalizationManager实例
     */
    public static LocalizationManager getInstance() {
        if (instance == null) {
            instance = new LocalizationManager();
        }
        return instance;
    }
    
    /**
     * 初始化支持的语言和对应的Locale
     */
    private void initializeLocales() {
        locales = new HashMap<>();
        locales.put("English", new Locale("en", "US"));
        locales.put("中文 (Chinese)", new Locale("zh", "CN"));
        locales.put("Español (Spanish)", new Locale("es", "ES"));
    }
    
    /**
     * 加载当前语言的资源包
     */
    private void loadResourceBundle() {
        try {
            Locale locale = locales.get(currentLanguage);
            if (locale == null) {
                locale = Locale.ENGLISH; // 默认为英语
            }
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        } catch (Exception e) {
            e.printStackTrace();
            // 如果加载失败，使用默认语言
            resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
        }
    }
    
    /**
     * 设置当前语言
     * @param language 要设置的语言名称
     * @return 是否成功设置语言
     */
    public boolean setLanguage(String language) {
        if (!locales.containsKey(language)) {
            return false;
        }
        
        currentLanguage = language;
        loadResourceBundle();
        return true;
    }
    
    /**
     * 获取指定键的本地化文本
     * @param key 资源键
     * @return 本地化文本，如果键不存在则返回键名
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return key; // 如果找不到键，返回键名本身
        }
    }
    
    /**
     * 获取当前语言
     * @return 当前语言名称
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    /**
     * 根据用户偏好设置应用语言
     * @param user 当前用户
     */
    public void applyUserLanguagePreference(User user) {
        if (user != null) {
            setLanguage(user.getLanguage());
        }
    }
    
    /**
     * 获取应用程序支持的所有语言
     * @return 语言名称数组
     */
    public String[] getSupportedLanguages() {
        return locales.keySet().toArray(new String[0]);
    }
} 