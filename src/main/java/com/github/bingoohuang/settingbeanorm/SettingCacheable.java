package com.github.bingoohuang.settingbeanorm;

public interface SettingCacheable {
    <T> T getSettings(Class<T> beanClass, String settingTable);
}
