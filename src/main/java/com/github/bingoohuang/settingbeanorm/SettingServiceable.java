package com.github.bingoohuang.settingbeanorm;

import java.util.List;

public interface SettingServiceable<T> {
    SettingUpdater getSettingUpdater();

    Class<T> getBeanClass();

    String getSettingTable();

    /**
     * 获取配置（用于业务逻辑判断）。
     */
    default T getSettingBean() {
        return getSettingUpdater().getSettingBean(getBeanClass(), getSettingTable());
    }

    /**
     * 获取配置项列表（用于配置页面）
     */
    default List<SettingItem> getSettingsItems() {
        return getSettingUpdater().getSettingsItems(getSettingTable());
    }

    /**
     * 更新配置。（适合直接单项配置的更新）
     */
    default void updateSettings(T settingBean) {
        getSettingUpdater().updateSettings(settingBean, getSettingTable());
    }

    /**
     * 更新配置。（适合从页面上多项配置同时更新）
     */
    default void updateSettings(List<SettingItem> changes) {
        getSettingUpdater().updateSettings(getBeanClass(), changes, getSettingTable());
    }

    default void clearSettingsCache() {
        getSettingUpdater().clearSettingsCache(getBeanClass(), getSettingTable());
    }
}
