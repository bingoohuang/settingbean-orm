package com.github.bingoohuang.settingbeanorm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.github.bingoohuang.utils.joda.JodaDateTimeDeserializer;
import com.github.bingoohuang.utils.joda.JodaDateTimeSerializer;
import lombok.val;
import org.joda.time.DateTime;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class Jsons {
    @SuppressWarnings("unchecked")
    public static <T> T parseJson(String json, Field field) {
        val config = new ParserConfig();
        config.putDeserializer(DateTime.class, new JodaDateTimeDeserializer());
        config.setAutoTypeSupport(true);

        val genericType = parseGenericArg0Type(field.getGenericType());
        return (T) JSON.parseObject(json, genericType, config);
    }

    public static Type parseGenericArg0Type(Type genericType) {
        if (!(genericType instanceof ParameterizedType)) return genericType;

        val pt = (ParameterizedType) genericType;
        if (pt.getRawType() != Map.class) return genericType;

        val args = pt.getActualTypeArguments();
        val ownerType = pt.getOwnerType();
        return new ParameterizedTypeImpl(args, ownerType, LinkedHashMap.class);
    }

    public static String json(Object value) {
        val config = new SerializeConfig();
        config.put(DateTime.class, new JodaDateTimeSerializer("yyyy-MM-dd HH:mm:ss.SSS", false, false));

        return JSON.toJSONString(value, config);
    }
}
