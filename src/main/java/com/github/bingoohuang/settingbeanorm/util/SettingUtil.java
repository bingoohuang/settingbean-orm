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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.github.bingoohuang.settingbeanorm.util.FieldValuePopulator.populate;

@Slf4j
public class SettingUtil {
    public static String firstNoneEmpty(String... values) {
        for (val value : values) {
            if (StringUtils.isNotEmpty(value)) return value;
        }

        throw new RuntimeException("No non-empty values");
    }

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
        val map = items.stream().collect(Collectors.toMap(x -> x.getName(), x -> x));
        T bean = Reflect.on(beanClass).create().get();
        for (val f : beanClass.getDeclaredFields()) {
            if (SettingUtil.isIgnored(f)) continue;

            val sf = getSettingField(f);
            if (sf.ignored()) continue;

            val item = map.get(firstNoneEmpty(sf.name(), sf.value(), f.getName()));
            if (item != null) {
                populate(f, bean, item.getValue(), sf.format(), sf.timeUnit());
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


    public static boolean isIgnored(Field f) {
        if (f.isSynthetic()) return true;
        // ignore un-normal fields like $jacocoData
        if (f.getName().startsWith("$")) return true;
        return false;
    }
}
