package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.SettingField;
import com.github.bingoohuang.settingbeanorm.SettingItem;
import com.github.bingoohuang.settingbeanorm.SettingValueFormat;
import com.github.bingoohuang.utils.lang.Str;
import com.github.bingoohuang.utils.reflect.Fields;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joor.Reflect;
import org.n3r.eql.base.AfterPropertiesSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.bingoohuang.settingbeanorm.util.FieldValueSetter.populate;

@Slf4j
public class SettingUtil {
    public static SettingField getSettingField(Field f) {
        SettingField field = f.getAnnotation(SettingField.class);
        if (field != null) return field;

        return new SettingField() {
            @Override public Class<? extends Annotation> annotationType() {
                return SettingField.class;
            }

            @Override public String value() {
                return f.getName();
            }

            @Override public String name() {
                return f.getName();
            }

            @Override public String title() {
                return f.getName();
            }

            @Override public SettingValueFormat format() {
                return SettingValueFormat.Default;
            }

            @Override public TimeUnit timeUnit() {
                return TimeUnit.SECONDS;
            }

            @Override public boolean ignored() {
                return false;
            }
        };
    }

    public static <T> T populateBean(Class<T> beanClass, List<SettingItem> items) {
        val map = items.stream().collect(Collectors.toMap(SettingItem::getName, x -> x));
        T bean = Reflect.on(beanClass).create().get();
        for (val f : beanClass.getDeclaredFields()) {
            if (SettingUtil.isIgnored(f)) continue;

            val sf = getSettingField(f);
            val item = map.get(Str.firstNoneEmpty(sf.name(), sf.value(), f.getName()));
            if (item != null) {
                populate(f, bean, item.getValue(), sf.format(), sf.timeUnit());
            }
        }

        if (bean instanceof AfterPropertiesSet) {
            ((AfterPropertiesSet) bean).afterPropertiesSet();
        }

        return bean;
    }

    @SuppressWarnings("unchecked")
    public static boolean isIgnored(Field f) {
        if (Fields.shouldIgnored(f)) return true;

        val sf = f.getAnnotation(SettingField.class);
        return sf != null && sf.ignored();
    }
}
