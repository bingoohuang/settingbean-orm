package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.spring.SettingBeanDao;
import com.github.bingoohuang.settingbeanorm.util.SettingUtil;
import com.github.bingoohuang.settingbeanorm.util.UpdaterImpl;
import com.google.common.eventbus.EventBus;
import lombok.val;

import java.util.List;

public abstract class SettingServiceable {
    protected abstract Class<?> getBeanClass();

    protected abstract String getSettingTable();

    protected abstract SettingBeanDao getSettingBeanDao();

    protected abstract void clearSettingsCache();

    /**
     * 获取配置（用于业务逻辑判断）。
     */
    protected abstract <T> T getSettingBean();


    protected Object getUncachedSettingBean() {
        val items = getSettingBeanDao().querySettingItems(getSettingTable());
        return SettingUtil.populateBean(getBeanClass(), items);
    }

    // For setting changes notify
    private EventBus eventBus = new EventBus();

    public void registerSettingUpdate(Object receiver) {
        eventBus.register(receiver);
    }

    public void unregisterSettingChange(Object receiver) {
        eventBus.unregister(receiver);
    }

    /**
     * 获取配置项列表（用于配置页面）
     */
    public List<SettingItem> getSettingsItems() {
        return getSettingBeanDao().querySettingItems(getSettingTable());
    }

    /**
     * 更新配置。（适合直接单项配置的更新）
     */
    public void updateSettings(Object settingBean) {
        String settingTable = getSettingTable();
        val updater = new UpdaterImpl(getSettingBeanDao(), settingTable);
        val old = getSettingBean();

        if (updater.update(settingBean)) {
            clearSettingsCache();
            val nes = getSettingBean();
            eventBus.post(new SettingUpdateEvent(old, nes));
        }
    }

    /**
     * 更新配置。（适合从页面上多项配置同时更新）
     */
    public void updateSettings(List<SettingItem> changes) {
        String settingTable = getSettingTable();
        val updater = new UpdaterImpl(getSettingBeanDao(), settingTable);
        if (updater.update(changes)) {
            clearSettingsCache();
        }
    }
}
