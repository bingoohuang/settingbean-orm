package com.github.bingoohuang.settingbeanorm.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MinValidator implements ValueValidator, ValueValidatorOptionsAware {
    private String min;

    @Override public boolean isReady() {
        return StringUtils.isNotEmpty(min);
    }

    @Override public void validate(String value) {
        if (min.compareTo(value) <= 0) return;

        throw new ValueValidateException(value + " failed with @Min(" + min + ")");
    }

    @Override public void applyOptions(List<String> options) {
        if (!options.isEmpty()) {
            this.min = options.get(0);
        }
    }
}
