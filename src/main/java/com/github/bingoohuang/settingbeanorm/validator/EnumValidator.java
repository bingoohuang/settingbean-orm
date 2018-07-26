package com.github.bingoohuang.settingbeanorm.validator;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class EnumValidator implements ValueValidator, ValueValidatorOptionsAware {
    private Set<String> options;

    @Override public boolean isReady() {
        return options != null && !options.isEmpty();
    }

    @Override public void validate(String value) {
        if (options.contains(value)) return;

        throw new ValueValidateException(value + " failed with @Enum(" + Joiner.on(", ").join(options) + ")");
    }

    @Override public void applyOptions(List<String> options) {
        this.options = Sets.newHashSet(options);
    }
}
