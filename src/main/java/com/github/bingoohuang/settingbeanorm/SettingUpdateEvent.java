package com.github.bingoohuang.settingbeanorm;

import lombok.Value;

@Value
public class SettingUpdateEvent<T> {
    private final T oldSettingBbean;
    private final T newSettingBean;

    public SettingUpdateEvent(T oldSettingBbean, T newSettingBean) {
        this.oldSettingBbean = oldSettingBbean;
        this.newSettingBean = newSettingBean;
    }
}
