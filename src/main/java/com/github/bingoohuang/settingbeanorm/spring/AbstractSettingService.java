package com.github.bingoohuang.settingbeanorm.spring;

import com.github.bingoohuang.settingbeanorm.SettingServiceable;
import com.github.bingoohuang.settingbeanorm.SettingUpdater;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractSettingService<T> implements SettingServiceable<T> {
    final Class<T> beanClass;
    final String settingTable;

    @Autowired SettingUpdater updater;

    public AbstractSettingService(Class<T> beanClass, String settingTable) {
        this.beanClass = beanClass;
        this.settingTable = settingTable;
    }

    public SettingUpdater settingUpdater() {
        return updater;
    }

    public String settingTable() {
        return settingTable;
    }

    public Class<T> beanClass() {
        return beanClass;
    }
}
