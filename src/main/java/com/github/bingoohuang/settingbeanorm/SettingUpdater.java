package com.github.bingoohuang.settingbeanorm;

import com.github.bingoohuang.settingbeanorm.util.FieldValuePopulator;
import com.github.bingoohuang.settingbeanorm.util.SettingUtil;
import com.github.bingoohuang.settingbeanorm.validator.ValueValidatorSpec;
import com.github.bingoohuang.westcache.utils.WestCacheConnector;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SettingUpdater {
    private final SettingBeanDao settingBeanDao;
    private final SettingCacheable settingCacheable;

    /**
     * 获取租户配置（用于业务逻辑判断）。
     */
    public <T> T getSettingBean(Class<T> beanClass, String settingTable) {
        return settingCacheable.getSettings(beanClass, settingTable);
    }

    /**
     * 获取租户配置项列表（用于配置页面）
     */
    public List<SettingItem> getSettingsItems(String settingTable) {
        return settingBeanDao.querySettingItems(settingTable);
    }

    /**
     * 更新租户配置。（适合直接单项配置的更新）
     */
    @SneakyThrows
    public <T> void updateSettings(T settingBean, String settingTable) {
        List<SettingItem> changes = Lists.newArrayList();
        List<SettingItem> news = Lists.newArrayList();

        val settingsItems = settingBeanDao.querySettingItems(settingTable).stream().collect(Collectors.toMap(x -> x.getName(), x -> x));
        for (val f : settingBean.getClass().getDeclaredFields()) {
            if (!f.isAccessible()) f.setAccessible(true);

            val a = f.getAnnotation(SettingField.class);
            if (a != null && a.ignored()) continue;

            val settingName = SettingUtil.getSettingName(a, f);
            val settingTitle = SettingUtil.getSettingTitle(a, settingName);
            val format = SettingUtil.getFormat(a);
            val unit = SettingUtil.getTimeUnit(a);
            val settingValue = FieldValuePopulator.fieldToString(f, f.get(settingBean), format, unit);
            detectChanged(settingsItems, settingName, settingValue, settingTitle, changes, news);
        }

        saveUpdatedSettings(settingBean.getClass(), changes, news, settingTable);
    }

    /**
     * 更新租户配置。（适合从页面上多项配置同时更新）
     */
    public void updateSettings(Class<?> settingBeanClass, List<SettingItem> changes, String settingTable) {
        List<SettingItem> changed = Lists.newArrayList();
        List<SettingItem> news = Lists.newArrayList();

        val settingsItems = settingBeanDao.querySettingItems(settingTable).stream().collect(Collectors.toMap(x -> x.getName(), x -> x));
        for (val c : changes) {
            detectChanged(settingsItems, c.getName(), c.getValue(), c.getTitle(), changed, news);
        }

        saveUpdatedSettings(settingBeanClass, changed, news, settingTable);
    }


    private void detectChanged(Map<String, SettingItem> items, String name, String value, String title, List<SettingItem> changes, List<SettingItem> news) {
        val item = items.get(name);
        if (item == null) {
            news.add(SettingItem.builder()
                    .name(name).value(value).title(title).editable(true)
                    .createTime(DateTime.now()).updateTime(DateTime.now())
                    .build());
            return;
        }

        if (item.getValue().equals(value)) return;
        if (!item.isEditable()) throw new RuntimeException("setting " + item + " is not editable");

        if (StringUtils.isNotEmpty(item.getSpec()))
            ValueValidatorSpec.from(item.getSpec()).validate(value);

        item.setValue(value);
        changes.add(item);
    }

    private void saveUpdatedSettings(Class<?> settingBeanClass, List<SettingItem> changed, List<SettingItem> news, String settingTable) {
        if (changed.isEmpty() && news.isEmpty()) return;

        if (!changed.isEmpty()) {
            settingBeanDao.updateSettings(changed, settingTable);
        }
        if (!news.isEmpty()) {
            settingBeanDao.addSettings(news, settingTable);
        }

        clearSettingsCache(settingBeanClass, settingTable);
    }

    public void clearSettingsCache(Class<?> beanClass, String settingTable) {
        WestCacheConnector.clearCache(() -> settingCacheable.getSettings(beanClass, settingTable));
    }
}
