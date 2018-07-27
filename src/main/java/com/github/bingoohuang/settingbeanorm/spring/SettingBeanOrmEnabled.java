package com.github.bingoohuang.settingbeanorm.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SettingBeanOrmConfig.class)
public @interface SettingBeanOrmEnabled {

}
