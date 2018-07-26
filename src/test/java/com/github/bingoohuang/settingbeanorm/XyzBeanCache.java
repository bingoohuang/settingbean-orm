package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.util.SettingUtil;
import com.github.bingoohuang.westcache.WestCacheable;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XyzBeanCache implements SettingCacheable {
    @Autowired XyzBeanDao xyzBeanDao;

    @WestCacheable(manager = "redis")
    @Override public <T> T getSettings(Class<T> beanClass, String settingTable) {
        val items = xyzBeanDao.querySettingItems(settingTable);
        T t = SettingUtil.populateBean(beanClass, items);
        return SettingUtil.autowire(t);
    }
}
