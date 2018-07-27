package com.github.bingoohuang.settingbeanorm.xyz;

import com.github.bingoohuang.settingbeanorm.SettingCacheable;
import com.github.bingoohuang.westcache.WestCacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XyzBeanCache implements SettingCacheable<XyzSetting> {
    @Autowired XyzBeanDao xyzBeanDao;

    @WestCacheable(manager = "redis")
    @Override public XyzSetting getSettings(Class<?> beanClass, String settingTable) {
        return getSettings(XyzSetting.class, settingTable, xyzBeanDao);
    }
}
