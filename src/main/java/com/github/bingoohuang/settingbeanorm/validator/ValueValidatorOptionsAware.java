package com.github.bingoohuang.settingbeanorm.validator;

import java.util.List;

public interface ValueValidatorOptionsAware {
    void applyOptions(List<String> options);
}
