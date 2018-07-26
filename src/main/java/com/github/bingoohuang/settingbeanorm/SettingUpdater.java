package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.util.UpdaterImpl;
import com.github.bingoohuang.westcache.utils.WestCacheConnector;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.util.List;

@RequiredArgsConstructor
public class SettingUpdater {
    private final SettingBeanDao settingBeanDao;
    private final SettingCacheable settingCacheable;

    /**
     * 获取租户配置（用于业务逻辑判断）。
     */
    public <T> T getSettingBean(Class<T> beanClass, String settingTable) {
        return settingCacheable.getSettings(beanClass, settingTable);
    }

    /**
     * 获取租户配置项列表（用于配置页面）
     */
    public List<SettingItem> getSettingsItems(String settingTable) {
        return settingBeanDao.querySettingItems(settingTable);
    }

    /**
     * 更新租户配置。（适合直接单项配置的更新）
     */
    @SneakyThrows
    public void updateSettings(Object settingBean, String settingTable) {
        val updater = new UpdaterImpl(settingBeanDao, settingTable);
        if (updater.update(settingBean)) {
            clearSettingsCache(settingBean.getClass(), settingTable);
        }

    }

    /**
     * 更新租户配置。（适合从页面上多项配置同时更新）
     */
    public void updateSettings(Class<?> settingBeanClass, List<SettingItem> changes, String settingTable) {
        val updater = new UpdaterImpl(settingBeanDao, settingTable);
        if (updater.update(changes)) {
            clearSettingsCache(settingBeanClass, settingTable);
        }
    }


    public void clearSettingsCache(Class<?> beanClass, String settingTable) {
        WestCacheConnector.clearCache(() -> settingCacheable.getSettings(beanClass, settingTable));
    }
}
