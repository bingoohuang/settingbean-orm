package com.github.bingoohuang.settingbeanorm;

public enum SettingValueFormat {
    Default,
    /**
     * Simple comma-separated list.
     */
    SimpleList,

    /**
     * Time duration for human friendly.
     * like 1h for 1 hour, 10m for 10 minutes.
     * The unit only support h(hour, default), m(minute).
     */
    HumanTimeDuration,
}
