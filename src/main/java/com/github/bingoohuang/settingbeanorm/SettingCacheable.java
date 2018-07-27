package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.util.SettingUtil;
import lombok.val;

public interface SettingCacheable<T> {
     T getSettings(Class<?> beanClass, String settingTable);

     default T getSettings(Class<T> beanClass, String settingTable, SettingBeanDao settingBeanDao) {
        val items = settingBeanDao.querySettingItems(settingTable);
        T t = SettingUtil.populateBean(beanClass, items);
        return SettingUtil.autowire(t);
    }
}
