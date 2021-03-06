package com.github.bingoohuang.settingbeanorm.spring;

import com.github.bingoohuang.settingbeanorm.SettingServiceable;
import com.github.bingoohuang.utils.lang.Classpath;
import com.github.bingoohuang.utils.lang.Clz;
import com.github.bingoohuang.westcache.WestCacheable;
import com.github.bingoohuang.westcache.utils.WestCacheConnector;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SettingService extends SettingServiceable implements ApplicationContextAware {
    @Autowired private SettingBeanDao settingBeanDao;

    private Class<?> beanClass;
    private String settingTable;
    private ApplicationContext appContext;

    @PostConstruct @SneakyThrows
    public void postConstruct() {
        val p = Classpath.loadProperties("settingbean-orm.properties");

        val beanClassName = p.getProperty("BeanClass");
        this.beanClass = Clz.findClass(beanClassName);
        if (this.beanClass == null) {
            throw new RuntimeException("BeanClass for setting is not well configured!");
        }

        this.settingTable = p.getProperty("SettingTable", "T_SETTING");
    }

    @Override public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override public String getSettingTable() {
        return settingTable;
    }

    @Override public SettingBeanDao getSettingBeanDao() {
        return settingBeanDao;
    }

    @Override public void clearSettingsCache() {
        // 不能从自身调用，否则方法代理不起作用，所以需要借道另外的类来完成
        appContext.getBean(SettingServiceClearCache.class).clearSettingsCache();
    }

    @SuppressWarnings("unchecked")
    @WestCacheable(manager = "redis")
    @Override public <T> T getSettingBean() {
        return (T) getUncachedSettingBean();
    }

    @Override public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.appContext = appContext;
    }

    // 本类纯粹是为了完成清除缓存功能
    @Component
    public static class SettingServiceClearCache {
        @Autowired SettingService settingService;

        void clearSettingsCache() {
            WestCacheConnector.clearCache(() -> settingService.getSettingBean());
        }
    }
}
