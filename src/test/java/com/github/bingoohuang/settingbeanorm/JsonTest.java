package com.github.bingoohuang.settingbeanorm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.github.bingoohuang.settingbeanorm.util.JsonJodaDeserializer;
import com.github.bingoohuang.settingbeanorm.util.JsonJodaSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class JsonTest {
    @Test
    public void testSerialize1() {
        val config = new SerializeConfig();
        config.put(DateTime.class, new JsonJodaSerializer());

        String time = "2018-07-26 11:38:57.123";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo('"' + time + '"');
    }

    @Test
    public void testSerialize2() {
        val config = new SerializeConfig();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        config.put(DateTime.class, new JsonJodaSerializer(pattern, false));

        String time = "2018-07-26 11:38:57";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo('"' + time + '"');
    }

    @Test
    public void testSerialize3() {
        val config = new SerializeConfig();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        config.put(DateTime.class, new JsonJodaSerializer("", true));

        String time = "2018-07-26 11:38:57";
        val dt = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        String json = JSON.toJSONString(dt, config);
        assertThat(json).isEqualTo(dt.getMillis() + "");
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DateTimeBean {
        private DateTime dateTime;
    }

    @Test
    public void testSerializeNull1() {
        val config = new SerializeConfig();
        config.put(DateTime.class, new JsonJodaSerializer("", true));

        String json = JSON.toJSONString(new DateTimeBean(null), config);
        assertThat(json).isEqualTo("{}");
    }


    @Test
    public void testDeserialize1() {
        val config = new ParserConfig();
        config.putDeserializer(DateTime.class, new JsonJodaDeserializer("yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
        String time = "2018-07-26 11:38:57";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        val dt1 = DateTime.parse(time, DateTimeFormat.forPattern(pattern));

        DateTime dt2 = JSON.parseObject(dt1.getMillis() + "", DateTime.class, config);
        assertThat(dt2).isEqualTo(dt1);

        DateTime dt3 = JSON.parseObject('"' + time + '"', DateTime.class, config);
        assertThat(dt3).isEqualTo(dt1);
    }
}
