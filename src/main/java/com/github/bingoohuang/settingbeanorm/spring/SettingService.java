package com.github.bingoohuang.settingbeanorm.spring;

import com.github.bingoohuang.settingbeanorm.SettingServiceable;
import com.github.bingoohuang.settingbeanorm.SettingUpdater;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SettingService<T> implements SettingServiceable<T> {
    @Getter final Class<T> beanClass;
    @Getter final String settingTable;
    @Getter final SettingUpdater settingUpdater;
}
