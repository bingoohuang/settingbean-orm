package com.github.bingoohuang.settingbeanorm.util;

import lombok.Builder;
import lombok.Value;

import java.util.concurrent.TimeUnit;

@Value @Builder
public class TimeDuration {
    private final double value;
    private final TimeUnit unit;

    @Override public String toString() {
        return value + unit.toString();
    }
}
