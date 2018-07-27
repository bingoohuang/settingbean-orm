package com.github.bingoohuang.settingbeanorm.xyz;

import com.github.bingoohuang.settingbeanorm.SettingField;
import com.github.bingoohuang.settingbeanorm.SettingValueFormat;
import com.github.bingoohuang.settingbeanorm.util.BusinessTime;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.n3r.eql.base.AfterPropertiesSet;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class XyzSetting implements AfterPropertiesSet {
    private int maxSubscribesPerMember;
    private boolean allowQueuing;
    @SettingField(name = "CANCEL_SUBSCRIPTION_MIN_BEFORE_HOURS",
            format = SettingValueFormat.HumanTimeDuration, timeUnit = TimeUnit.MINUTES)
    private int cancelSubscriptionMinBeforeMinutes = 0;  // 取消预约最少提前的小时数
    @SettingField(ignored = true)
    private String cancelSubscriptionMinBeforeReadable;  // 取消预约最少提前的小时数

    @SettingField(title = "测试配置XX")
    private int xx = -1;

    @SettingField(format = SettingValueFormat.SimpleList)
    private List<String> themes = ImmutableList.of("#333");  // 场馆可选主题色列表

    private BusinessTime businessTime = new BusinessTime("09:00", "19:00");

    @Override public void afterPropertiesSet() {
        if (xx == -1) xx = 100;

        cancelSubscriptionMinBeforeReadable = readable(cancelSubscriptionMinBeforeMinutes);
    }

    private String readable(int minutes) {
        return minutes >= 60 && minutes % 60 == 0 ? minutes / 60 + "小时" : minutes + "分钟";
    }
}
