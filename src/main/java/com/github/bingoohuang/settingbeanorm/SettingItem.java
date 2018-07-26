package com.github.bingoohuang.settingbeanorm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class SettingItem {
    private String name;
    private String value;
    private String title;
    private boolean editable;
    private String spec;
    private DateTime createTime;
    private DateTime updateTime;
}
