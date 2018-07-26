package com.github.bingoohuang.settingbeanorm.validator;

import com.github.bingoohuang.utils.lang.Clz;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class ValueValidatorFactory {

    public static ValueValidator createValidator(String validatorName, String options) {
        String className = DigitsValidator.class.getPackage().getName()  + "." + validatorName + "Validator";
        Class<?> aClass = Clz.findClass(className);
        if (aClass == null) {
            log.warn("@{} is not defined", validatorName);
            return null;
        }
        Object instance;
        try {
            instance = aClass.newInstance();
        } catch (Exception ex) {
            log.warn("@{} is not well defined", validatorName, ex);
            return null;
        }

        if (!(instance instanceof ValueValidator)) {
            log.warn("@{} is not well defined", validatorName);
            return null;
        }

        if (StringUtils.isNotEmpty(options)) {
            if (instance instanceof ValueValidatorOptionsAware) {
                List<String> opts = Splitter.on(',').trimResults().splitToList(options);
                try {
                    ((ValueValidatorOptionsAware) instance).applyOptions(opts);
                } catch (Exception ex) {
                    log.warn("@{} do not accept option {}", validatorName, options, ex);
                    return null;
                }
            } else {
                log.warn("@{} do not accept option {}", validatorName, options);
            }
        }


        val validator = (ValueValidator) instance;
        if (validator.isReady()) return validator;

        if (StringUtils.isNotEmpty(options)) {
            log.warn("@{}({}) is not ready", validatorName, options);
        } else {
            log.warn("@{} is not ready", validatorName);
        }

        return null;
    }
}
