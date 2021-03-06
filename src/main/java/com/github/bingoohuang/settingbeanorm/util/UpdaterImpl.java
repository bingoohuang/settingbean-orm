package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.SettingItem;
import com.github.bingoohuang.settingbeanorm.spring.SettingBeanDao;
import com.github.bingoohuang.settingbeanorm.validator.ValueValidatorSpec;
import com.github.bingoohuang.utils.lang.Str;
import com.github.bingoohuang.utils.reflect.Fields;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.bingoohuang.settingbeanorm.util.FieldValueSetter.fieldToString;

public class UpdaterImpl {
    private final Map<String, SettingItem> settingsItems;
    @Getter private final List<SettingItem> changes = Lists.newArrayList();
    @Getter private final List<SettingItem> news = Lists.newArrayList();
    private final String settingTable;
    private final SettingBeanDao settingBeanDao;


    public UpdaterImpl(SettingBeanDao settingBeanDao, String settingTable) {
        this.settingTable = settingTable;
        this.settingBeanDao = settingBeanDao;
        this.settingsItems = settingBeanDao.querySettingItems(settingTable).stream()
                .collect(Collectors.toMap(SettingItem::getName, x -> x));

    }

    public boolean update(List<SettingItem> items) {
        for (val c : items) {
            detectChanged(settingsItems.get(c.getName()), c.getName(), c.getValue(), c.getTitle());
        }

        return saveUpdatedSettings();
    }

    @SneakyThrows
    public boolean update(Object settingBean) {
        for (val f : settingBean.getClass().getDeclaredFields()) {
            if (SettingUtil.isIgnored(f)) continue;

            Fields.setAccessible(f);

            val sf = SettingUtil.getSettingField(f);
            val settingName = Str.firstNoneEmpty(sf.name(), sf.value(), f.getName());
            val settingTitle = Str.firstNoneEmpty(sf.title(), settingName);
            val settingValue = fieldToString(f, f.get(settingBean), sf.format(), sf.timeUnit());
            detectChanged(settingsItems.get(settingName), settingName, settingValue, settingTitle);
        }

        return saveUpdatedSettings();
    }


    private void detectChanged(SettingItem item, String name, String value, String title) {
        if (item == null) {
            news.add(SettingItem.builder()
                    .name(name).value(value).title(title).editable(true)
                    .createTime(DateTime.now()).updateTime(DateTime.now())
                    .build());
            return;
        }

        if (Objects.equals(item.getValue(), value)) return;
        if (!item.isEditable()) throw new RuntimeException("setting " + item + " is not editable");

        if (StringUtils.isNotEmpty(item.getSpec()))
            ValueValidatorSpec.from(item.getSpec()).validate(value);

        item.setValue(value);
        changes.add(item);
    }

    private boolean saveUpdatedSettings() {
        if (changes.isEmpty() && news.isEmpty()) return false;

        if (!changes.isEmpty()) {
            settingBeanDao.updateSettings(changes, settingTable);
        }
        if (!news.isEmpty()) {
            settingBeanDao.addSettings(news, settingTable);
        }

        return true;
    }
}
