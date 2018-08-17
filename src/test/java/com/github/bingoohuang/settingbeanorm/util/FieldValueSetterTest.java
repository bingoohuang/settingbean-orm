package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.xyz.XyzSetting;
import lombok.SneakyThrows;
import org.junit.Test;

import java.lang.reflect.Field;

public class FieldValueSetterTest {
    @Test(expected = RuntimeException.class) @SneakyThrows
    public void parseDefault() {
        Field field = XyzSetting.class.getDeclaredField("cardTypeNamesInShowOrder");
        FieldValueSetter.parseDefault(field, "{x");
    }
}