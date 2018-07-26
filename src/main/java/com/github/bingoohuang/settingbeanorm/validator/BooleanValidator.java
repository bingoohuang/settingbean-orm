package com.github.bingoohuang.settingbeanorm.validator;

import org.apache.commons.lang3.StringUtils;

public class BooleanValidator implements ValueValidator {
    @Override public boolean isReady() {
        return true;
    }

    @Override public void validate(String value) {
        if (StringUtils.equalsAnyIgnoreCase(value, "true", "false")) return;

        throw new ValueValidateException(value + " failed with @Boolean");
    }

}
