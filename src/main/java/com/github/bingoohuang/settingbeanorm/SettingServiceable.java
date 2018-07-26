package com.github.bingoohuang.settingbeanorm;

import java.util.List;

public interface SettingServiceable<T> {
    SettingUpdater settingUpdater();

    Class<T> beanClass();

    String settingTable();

    /**
     * 获取配置（用于业务逻辑判断）。
     */
    default T getSettingBean() {
        return settingUpdater().getSettingBean(beanClass(), settingTable());
    }

    /**
     * 获取配置项列表（用于配置页面）
     */
    default List<SettingItem> getSettingsItems() {
        return settingUpdater().getSettingsItems(settingTable());
    }

    /**
     * 更新配置。（适合直接单项配置的更新）
     */
    default void updateSettings(T settingBean) {
        settingUpdater().updateSettings(settingBean, settingTable());
    }

    /**
     * 更新配置。（适合从页面上多项配置同时更新）
     */
    default void updateSettings(List<SettingItem> changes) {
        settingUpdater().updateSettings(beanClass(), changes, settingTable());
    }

    default void clearSettingsCache() {
        settingUpdater().clearSettingsCache(beanClass(), settingTable());
    }
}
