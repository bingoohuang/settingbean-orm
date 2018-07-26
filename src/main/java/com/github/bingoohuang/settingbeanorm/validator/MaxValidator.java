package com.github.bingoohuang.settingbeanorm.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaxValidator implements ValueValidator, ValueValidatorOptionsAware {
    private String max;

    @Override public boolean isReady() {
        return StringUtils.isNotEmpty(max);
    }

    @Override public void validate(String value) {
        if (value.compareTo(max) <= 0) return;

        throw new ValueValidateException(value + " failed with @Max(" + max + ")");
    }

    @Override public void applyOptions(List<String> options) {
        if (!options.isEmpty()) {
            this.max = options.get(0);
        }
    }
}
