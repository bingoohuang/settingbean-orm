package com.github.bingoohuang.settingbeanorm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.github.bingoohuang.settingbeanorm.util.JsonJodaDeserializer;
import lombok.Data;
import org.joda.time.DateTime;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FastJsonTest {
    @Data
    public static class Other {
        private String name;
    }

    @Data
    public static class Bean {
        private Other key;
    }

    @Test
    public void test1() {
        String json = "{\"key\":\"\"}";

        Bean bean = JSON.parseObject(json, Bean.class);
        assertThat(bean).isEqualTo(new Bean());
    }


    @Test
    public void test2() {
        Bean bean = new Bean();

        ValueFilter filter = (o, s, v) -> v == null && o.getClass().getPackage().getName().startsWith(FastJsonTest.class.getPackage().getName()) ? "" : v;

        String s = JSON.toJSONString(bean, filter);
        assertThat(s).isEqualTo("{\"key\":\"\"}");
    }

    @Data
    public static class TimeBean {
        private DateTime time;
    }

    @Test
    public void testDateTime() {
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.putDeserializer(DateTime.class, new JsonJodaDeserializer("yyyy-MM-dd HH:mm:ss.SSS", "yyyy.MM.dd HH:mm:ss.SSS"));

        TimeBean o = JSON.parseObject("{\"time\":\"2018.05.25\"}", TimeBean.class, parserConfig, JSON.DEFAULT_PARSER_FEATURE);

        assertThat(o.getTime()).isNotNull();
    }

}
