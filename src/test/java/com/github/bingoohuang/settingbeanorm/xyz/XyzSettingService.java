package com.github.bingoohuang.settingbeanorm.xyz;

import com.github.bingoohuang.settingbeanorm.SettingUpdater;
import com.github.bingoohuang.settingbeanorm.spring.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XyzSettingService extends SettingService<XyzSetting> {

    @Autowired
    public XyzSettingService(XyzBeanDao dao, XyzBeanCache cache) {
        super(XyzSetting.class, "X_SETTING", new SettingUpdater<>(dao, cache));
    }
}
