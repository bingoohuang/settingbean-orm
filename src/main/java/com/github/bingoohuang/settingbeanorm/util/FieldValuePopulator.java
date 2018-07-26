package com.github.bingoohuang.settingbeanorm.util;

import com.github.bingoohuang.settingbeanorm.SettingValueFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class FieldValuePopulator {
    @SneakyThrows
    public static void populate(Field field, Object bean, String value, SettingValueFormat format, TimeUnit timeUnit) {
        Object fieldValue = parseFieldValue(field, value, format, timeUnit);
        if (!field.isAccessible()) field.setAccessible(true);
        field.set(bean, fieldValue);
    }

    private static HashMap<Class<?>, Function<String, ?>> parser = new HashMap<Class<?>, Function<String, ?>>() {{
        put(boolean.class, Boolean::parseBoolean); // Support boolean literals too
        put(int.class, Integer::parseInt);
        put(long.class, Long::parseLong);
        put(double.class, Double::valueOf);
        put(String.class, String::valueOf);  // Handle String without special test
    }};

    private static Object parseFieldValue(Field field, String value, SettingValueFormat format, TimeUnit timeUnit) {
        switch (format) {
            case SimpleList:
                return parseSimpleList(field, value);
            case HumanTimeDuration:
                TimeDuration duration = parseTimeDuration(field.getName(), value);
                long l = convertUnit(timeUnit, duration);
                return convertNumberType(l, field);
            case Default:
            default:
                return parseDefault(field, value);
        }
    }

    private static Object convertNumberType(long l, Field field) {
        Class<?> type = field.getType();
        if (type == Long.class || type == long.class) return Long.valueOf(l);
        if (type == Integer.class || type == int.class) return Integer.valueOf((int) l);

        throw new RuntimeException("unsupported field number type for " + field);
    }

    private static long convertUnit(TimeUnit timeUnit, TimeDuration duration) {
        double fractional = duration.getValue() % 1;
        if (fractional < 0.001) return timeUnit.convert((long) duration.getValue(), duration.getUnit());

        if (fractional - 0.5 < 0.001) {
            if (duration.getUnit() == TimeUnit.DAYS) {
                return timeUnit.convert((long) (duration.getValue() * 24), TimeUnit.HOURS);
            } else if (duration.getUnit() == TimeUnit.HOURS) {
                return timeUnit.convert((long) (duration.getValue() * 60), TimeUnit.MINUTES);
            } else if (duration.getUnit() == TimeUnit.MINUTES) {
                return timeUnit.convert((long) (duration.getValue() * 60), TimeUnit.SECONDS);
            }
        }

        throw new RuntimeException("bad format " + duration);
    }


    public static TimeDuration parseTimeDuration(String key, String spec) {
        checkArgument(spec != null && !spec.isEmpty(), "value of key %s omitted", key);

        try {
            char lastChar = spec.charAt(spec.length() - 1);
            if (lastChar >= '0' && lastChar <= '9') {
                return TimeDuration.builder().value(Double.parseDouble(spec)).unit(TimeUnit.HOURS).build();
            }

            String value = spec.substring(0, spec.length() - 1);
            double duration = Double.parseDouble(value);
            switch (lastChar) {
                case 'd':
                    return TimeDuration.builder().value(duration).unit(TimeUnit.DAYS).build();
                case 'h':
                    return TimeDuration.builder().value(duration).unit(TimeUnit.HOURS).build();
                case 'm':
                    return TimeDuration.builder().value(duration).unit(TimeUnit.MINUTES).build();
                case 's':
                    return TimeDuration.builder().value(duration).unit(TimeUnit.SECONDS).build();
                default:
                    throw new IllegalArgumentException(String.format("key %s invalid format.  was %s, " +
                            "must end with one of [dDhHmMsS]", key, spec));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", key, spec));
        }
    }

    private static Object parseSimpleList(Field field, String value) {
        checkTypeList(field);
        val values = Splitter.on(',').omitEmptyStrings().trimResults().splitToList(value);

        Class<?> argClas = parseListArgType(field);
        if (argClas == String.class) return values;

        val fun = parser.get(argClas);
        return fun != null ? values : values.stream().map(fun).collect(Collectors.toList());
    }

    private static Class<?> parseListArgType(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static void checkTypeList(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            throw new RuntimeException(field + "'type is not ParameterizedType");
        }

        val pt = (ParameterizedType) genericType;
        if (pt.getRawType() != List.class) {
            throw new RuntimeException(field + "'type is not List");
        }
    }

    private static Object parseDefault(Field field, String value) {
        val fun = parser.get(field.getType());
        if (fun != null) return fun.apply(value);

        return Jsons.parseJson(value, field);
    }

    public static String fieldToString(Field field, Object fieldValue, SettingValueFormat format, TimeUnit unit) {
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