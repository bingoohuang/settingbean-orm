package com.github.bingoohuang.settingbeanorm.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

@AllArgsConstructor
public class JsonJodaSerializer implements ObjectSerializer {
    private final String pattern;
    private final boolean useLong;

    public JsonJodaSerializer() {
        this("yyyy-MM-dd HH:mm:ss.SSS", false);
    }

    @Override
    public void write(JSONSerializer serializer, Object object,
                      Object fieldName, Type fieldType, int features) {
        val value = (DateTime) object;

        if (useLong) {
            serializer.out.writeLong(value.getMillis());
        } else {
            serializer.out.writeString(value.toString(pattern));
        }
    }
}
