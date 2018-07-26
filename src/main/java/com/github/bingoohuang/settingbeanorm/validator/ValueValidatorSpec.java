package com.github.bingoohuang.settingbeanorm.validator;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

@Slf4j
public class ValueValidatorSpec {
    public static ValueValidator from(String spec) {
        Iterable<String> specItems = Splitter.onPattern("\\s+").trimResults().split(spec);
        List<ValueValidator> itemValidators = Lists.newArrayList();

        for(val specItem : specItems) {
            if (!specItem.startsWith("@")) {
                log.warn("Bad spec item {} in spec {}", specItem, spec);
                continue;
            }

            String validatorName = specItem.substring(1);
            String options = "";
            int braceLeft = validatorName.indexOf('(');
            if (braceLeft > 0) {
                int braceRight = validatorName.indexOf(')', braceLeft);
                if (braceRight < 0) {
                    log.warn("Bad spec item {} in spec {}", specItem, spec);
                    continue;
                }

                options = validatorName.substring(braceLeft + 1, braceRight);
                validatorName = validatorName.substring(0, braceLeft);
            }


            val itemValidator =  ValueValidatorFactory.createValidator(validatorName, options);
            if (itemValidator != null) {
                itemValidators.add(itemValidator);
            }
        }

        return new ComponentValueValidator(itemValidators);
    }
}
