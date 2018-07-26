package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.SettingField;
import com.github.bingoohuang.settingbeanorm.SettingItem;
import com.github.bingoohuang.settingbeanorm.SettingValueFormat;
import com.github.bingoohuang.settingbeanorm.spring.Autowireable;
import com.github.bingoohuang.settingbeanorm.spring.Springs;
import com.github.bingoohuang.utils.joor.Reflect;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.n3r.eql.base.AfterPropertiesSet;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.bingoohuang.settingbeanorm.util.FieldValuePopulator.populate;


@Slf4j
public class SettingUtil {
    public static String getSettingName(SettingField a, Field f) {
        if (a == null) return f.getName();

        return firstNoneEmpty(a.name(), a.value(), f.getName());
    }

    private static String firstNoneEmpty(String... values) {
        for (val value : values) {
            if (StringUtils.isNotEmpty(value)) return value;
        }

        throw new RuntimeException("No non-empty values");
    }

    public static String getSettingTitle(SettingField a, String defaultValue) {
        return a == null ? defaultValue : firstNoneEmpty(a.title(), defaultValue);
    }

    public static SettingValueFormat getFormat(SettingField a) {
        return a == null ? SettingValueFormat.Default : a.format();
    }

    public static TimeUnit getTimeUnit(SettingField a) {
        return a == null ? null : a.timeUnit();
    }

    public static <T> T populateBean(Class<T> beanClass, List<SettingItem> items) {
        val itemsMap = items.stream().collect(Collectors.toMap(x -> x.getName(), x -> x));
        T bean = Reflect.on(beanClass).create().get();
        for (val f : beanClass.getDeclaredFields()) {
            val a = f.getAnnotation(SettingField.class);
            if (a != null && a.ignored()) continue;

            val item = itemsMap.get(getSettingName(a, f));
            if (item != null) {
                populate(f, bean, item.getValue(), getFormat(a), getTimeUnit(a));
            }
        }
        return bean;
    }


    public static <T> T autowire(T t) {
        if (t instanceof Autowireable) {
            Springs.inject(t);
        }

        if (t instanceof AfterPropertiesSet) {
            ((AfterPropertiesSet) t).afterPropertiesSet();
        }

        return t;
    }
}
