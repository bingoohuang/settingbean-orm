package com.github.bingoohuang.settingbeanorm;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingField {
    /**
     * Same to name.
     *
     * @return name
     */
    String value() default "";

    /**
     * Setting's name.
     *
     * @return name
     */
    String name() default "";

    /**
     * Title for this setting field.
     *
     * @return title
     */
    String title() default "";

    /**
     * Value's format for reading.
     *
     * @return Value's format
     */
    SettingValueFormat format() default SettingValueFormat.Default;


    /**
     * Using with format is SettingValueFormat.HumanTimeDuration.
     *
     * @return time unit
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Should ignored or not.
     *
     * @return ignored or not
     */
    boolean ignored() default false;
}
