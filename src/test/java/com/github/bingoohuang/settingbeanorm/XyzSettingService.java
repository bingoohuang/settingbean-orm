package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.spring.AbstractSettingService;
import org.springframework.stereotype.Service;

@Service
public class XyzSettingService extends AbstractSettingService<MySetting> {
    public XyzSettingService() {
        super(MySetting.class, "X_SETTING");
    }
}
