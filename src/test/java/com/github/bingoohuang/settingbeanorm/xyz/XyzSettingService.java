package com.github.bingoohuang.settingbeanorm.xyz;

import com.github.bingoohuang.settingbeanorm.SettingBeanDao;
import com.github.bingoohuang.settingbeanorm.SettingServiceable;
import com.github.bingoohuang.westcache.WestCacheable;
import com.github.bingoohuang.westcache.utils.WestCacheConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XyzSettingService extends SettingServiceable<XyzSetting> {
    @Autowired XyzBeanDao xyzBeanDao;
    @Autowired XyzSettingServiceClearCache xyzSettingServiceClearCache;

    @Override public Class<XyzSetting> getBeanClass() {
        return XyzSetting.class;
    }

    @Override public String getSettingTable() {
        return "X_SETTING";
    }

    @Override public SettingBeanDao getSettingBeanDao() {
        return xyzBeanDao;
    }

    @Override public void clearSettingsCache() {
        // 不能从自身调用，否则方法代理不起作用，所以需要借道另外的类来完成
        xyzSettingServiceClearCache.clearSettingsCache();
    }

    @WestCacheable(manager = "redis")
    @Override public XyzSetting getSettingBean() {
        return getUncachedSettingBean();
    }

    // 本类纯粹是为了完成清除缓存功能
    @Component
    static class XyzSettingServiceClearCache {
        @Autowired XyzSettingService xyzSettingService;

        void clearSettingsCache() {
            WestCacheConnector.clearCache(() -> xyzSettingService.getSettingBean());
        }
    }
}
