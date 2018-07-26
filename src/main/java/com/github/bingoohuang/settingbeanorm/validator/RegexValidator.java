package com.github.bingoohuang.settingbeanorm.validator;


import java.util.List;
import java.util.regex.Pattern;

public class RegexValidator implements ValueValidator, ValueValidatorOptionsAware {
    private String expr;
    private Pattern regex;

    @Override public boolean isReady() {
        return regex != null;
    }

    @Override public void validate(String value) {
        if (regex.matcher(value).matches()) return;

        throw new ValueValidateException(value + " failed with @Regex(" + expr + ")");
    }

    @Override public void applyOptions(List<String> options) {
        if (!options.isEmpty()) {
            this.expr = options.get(0);
            this.regex = Pattern.compile(expr);
        }
    }
}
