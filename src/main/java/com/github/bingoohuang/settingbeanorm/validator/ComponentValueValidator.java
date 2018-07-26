package com.github.bingoohuang.settingbeanorm.validator;

import java.util.List;

public class ComponentValueValidator implements ValueValidator {
    private final List<ValueValidator> validators;

    public ComponentValueValidator(List<ValueValidator> validators) {
        this.validators = validators;
    }

    @Override public boolean isReady() {
        return !validators.isEmpty();
    }

    @Override public void validate(String value) {
        for (ValueValidator validator : validators) {
            validator.validate(value);
        }
    }
}
