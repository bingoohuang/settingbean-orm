package com.github.bingoohuang.settingbeanorm.validator;

public interface ValueValidator {
    boolean isReady();

    void validate(String value);
}
