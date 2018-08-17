package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.SettingValueFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class FieldValueSetter {
    @SneakyThrows
    public static void populate(Field field, Object bean, String value,
                                SettingValueFormat format, TimeUnit timeUnit) {
        if (!field.isAccessible()) field.setAccessible(true);
        field.set(bean, parseFieldValue(field, value, format, timeUnit));
    }

    private static HashMap<Class<?>, Function<String, ?>> parser
            = new HashMap<Class<?>, Function<String, ?>>() {{
        put(boolean.class, Boolean::parseBoolean); // Support boolean literals too
        put(short.class, Short::parseShort);
        put(int.class, Integer::parseInt);
        put(long.class, Long::parseLong);
        put(float.class, Float::valueOf);
        put(double.class, Double::valueOf);
        put(String.class, FieldValueSetter::valueOfString);  // Handle String without special test
    }};

    public static String valueOfString(String s) {
        return s;
    }

    private static Object parseFieldValue(
            Field field, String value,
            SettingValueFormat format, TimeUnit timeUnit) {
        switch (format) {
            case SimpleList:
                return parseSimpleList(field, value);
            case HumanTimeDuration:
                val duration = parseTimeDuration(field.getName(), value);
                val l = convertUnit(timeUnit, duration);
                return convertNumberType(l, field);
            case Default:
            default:
                return parseDefault(field, value);
        }
    }

    private static Object convertNumberType(long l, Field field) {
        val type = field.getType();
        if (type == Long.class || type == long.class) return Long.valueOf(l);
        if (type == Integer.class || type == int.class) return Integer.valueOf((int) l);
        if (type == Short.class || type == short.class) return Short.valueOf((short) l);

        throw new RuntimeException("unsupported field number type for " + field);
    }

    private static long convertUnit(TimeUnit unit, TimeDuration dur) {
        val fractional = dur.getValue() % 1;
        if (fractional < 0.001) return unit.convert((long) dur.getValue(), dur.getUnit());

        if (fractional - 0.5 < 0.001) {
            if (dur.getUnit() == TimeUnit.DAYS) {
                return unit.convert((long) (dur.getValue() * 24), TimeUnit.HOURS);
            } else if (dur.getUnit() == TimeUnit.HOURS) {
                return unit.convert((long) (dur.getValue() * 60), TimeUnit.MINUTES);
            } else if (dur.getUnit() == TimeUnit.MINUTES) {
                return unit.convert((long) (dur.getValue() * 60), TimeUnit.SECONDS);
            }
        }

        throw new RuntimeException("bad format " + dur);
    }


    public static TimeDuration parseTimeDuration(String key, String spec) {
        checkArgument(spec != null && !spec.isEmpty(),
                "value of key %s omitted", key);

        try {
            val lastChar = spec.charAt(spec.length() - 1);
            if (lastChar >= '0' && lastChar <= '9') {
                return TimeDuration.builder().value(Double.parseDouble(spec))
                        .unit(TimeUnit.HOURS).build();
            }

            val value = spec.substring(0, spec.length() - 1);
            val duration = Double.parseDouble(value);
            val builder = TimeDuration.builder().value(duration);
            switch (lastChar) {
                case 'd':
                    return builder.unit(TimeUnit.DAYS).build();
                case 'h':
                    return builder.unit(TimeUnit.HOURS).build();
                case 'm':
                    return builder.unit(TimeUnit.MINUTES).build();
                case 's':
                    return builder.unit(TimeUnit.SECONDS).build();
                default:
                    throw new IllegalArgumentException(
                            String.format("key %s invalid format.  was %s, " +
                                    "must end with one of [dDhHmMsS]", key, spec));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "key %s value set to %s, must be integer", key, spec));
        }
    }

    private static Object parseSimpleList(Field field, String value) {
        checkTypeList(field);
        val values = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);

        val argClas = parseListArgType(field);
        if (argClas == String.class) return values;

        val fun = parser.get(argClas);
        return fun != null ? values : values.stream().map(fun).collect(Collectors.toList());
    }

    private static Class<?> parseListArgType(Field field) {
        val genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }

    public static void checkTypeList(Field field) {
        val genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new RuntimeException(field + "'type is not ParameterizedType");
        }

        val pt = (ParameterizedType) genericType;
        if (pt.getRawType() != List.class) {
            throw new RuntimeException(field + "'type is not List");
        }
    }

    public static Object parseDefault(Field field, String value) {
        val fun = parser.get(field.getType());
        if (fun != null) return fun.apply(value);

        if (StringUtils.isEmpty(value)) return null;

        try {
            return Jsons.parseJson(value, field);
        } catch (Exception e) {
            throw new RuntimeException("fail to parse json for field "
                    + field + " with value {" + value + "}", e);
        }
    }

    public static String fieldToString(
            Field field, Object fieldValue,
            SettingValueFormat format, TimeUnit unit) {
        if (fieldValue == null) return null;

        switch (format) {
            case SimpleList:
                return Joiner.on(',').join((List<?>) fieldValue);
            case HumanTimeDuration:
                return fieldValue + unitExpr(unit);
            case Default:
            default:
                return fieldToString(field, fieldValue);
        }
    }

    private static String unitExpr(TimeUnit unit) {
        if (unit == TimeUnit.DAYS) return "d";
        if (unit == TimeUnit.HOURS) return "h";
        if (unit == TimeUnit.MINUTES) return "m";

        throw new RuntimeException("bad unit " + unit);
    }

    private static String fieldToString(Field field, Object fieldValue) {
        val func = parser.get(field.getType());
        if (func != null) return fieldValue.toString();

        return Jsons.json(fieldValue);
    }
}
