package com.github.bingoohuang.settingbeanorm.validator;

public class DigitsValidator implements ValueValidator {
    @Override public boolean isReady() {
        return true;
    }

    @Override public void validate(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new ValueValidateException(value + " failed with @Digits");
        }
    }
}
