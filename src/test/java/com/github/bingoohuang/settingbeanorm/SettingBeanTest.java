package com.github.bingoohuang.settingbeanorm;


import com.google.common.collect.Lists;
import lombok.val;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.n3r.eql.Eql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class SettingBeanTest {
    @BeforeClass
    public static void beforeClass() {
        new Eql().execute("create table X_SETTING (" +
                "NAME varchar(100) not null, " +
                "VALUE varchar(100) not null, " +
                "TITLE varchar(100) not null, " +
                "EDITABLE int not null default 1, " +
                "SPEC varchar(100) null, " +
                "CREATE_TIME timestamp not null, " +
                "UPDATE_TIME timestamp not null);");

        new Eql().execute(
                "insert into X_SETTING values('maxSubscribesPerMember', '10', '单会员最大订课数量', 1, '@Digits @Min(1) @Max(20) @Regex(\\d+)', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into X_SETTING values('allowQueuing', 'true', '是否开启排队', 1, '@Boolean @Enum(true,false)', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into X_SETTING values('CANCEL_SUBSCRIPTION_MIN_BEFORE_HOURS', '0.5h', '至少提前多少时间取消预订', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into X_SETTING values('themes', 'red,blue,green', '主题颜色', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')");
    }

    @Autowired XyzSettingService xyzSettingService;


    @Test
    public void cache() {
        val settings = xyzSettingService.getSettingBean();
        settings.setAllowQueuing(!settings.isAllowQueuing());

        val settings2 = xyzSettingService.getSettingBean();
        assertThat(settings).isNotSameAs(settings2);
        assertThat(settings).isNotEqualTo(settings2);
    }


    @Test
    public void getSettings() {
        val setting = xyzSettingService.getSettingBean();
        val other = MySetting.builder().maxSubscribesPerMember(10).allowQueuing(true).xx(100)
                .cancelSubscriptionMinBeforeMinutes(30).cancelSubscriptionMinBeforeReadable("30分钟")
                .themes(Lists.newArrayList("red", "blue", "green"))
                .businessTime(new BusinessTime("09:00", "19:00"))
                .build();
        assertThat(setting).isEqualTo(other);

        setting.setMaxSubscribesPerMember(11);
        setting.setAllowQueuing(false);
        xyzSettingService.updateSettings(setting);

        val Settings2 = xyzSettingService.getSettingBean();
        val other2 = MySetting.builder().maxSubscribesPerMember(11).allowQueuing(false).xx(100)
                .cancelSubscriptionMinBeforeMinutes(30).cancelSubscriptionMinBeforeReadable("30分钟")
                .themes(Lists.newArrayList("red", "blue", "green"))
                .businessTime(new BusinessTime("09:00", "19:00"))
                .build();
        assertThat(Settings2).isEqualTo(other2);

        val SettingsItems = xyzSettingService.getSettingsItems();
        val item1 = SettingItem.builder()
                .name("maxSubscribesPerMember")
                .value("11")
                .title("单会员最大订课数量")
                .editable(true)
                .spec("@Digits @Min(1) @Max(20) @Regex(\\d+)")
                .createTime(DateTime.parse("2018-04-25 13:15:45", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .updateTime(DateTime.parse("2018-04-25 13:15:45", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        val item2 = SettingItem.builder()
                .name("allowQueuing")
                .value("false")
                .title("是否开启排队")
                .editable(true)
                .spec("@Boolean @Enum(true,false)")
                .createTime(DateTime.parse("2018-04-25 13:15:45", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .updateTime(DateTime.parse("2018-04-25 13:15:45", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        assertThat(SettingsItems).containsAllOf(item1, item2);

        item1.setValue("12");
        xyzSettingService.updateSettings(Lists.newArrayList(item1));

        val Settings3 = xyzSettingService.getSettingBean();
        assertThat(Settings3.getMaxSubscribesPerMember()).isEqualTo(12);
        assertThat(Settings3.getXx()).isEqualTo(100);
        assertThat(Settings3.getCancelSubscriptionMinBeforeMinutes()).isEqualTo(30);
        assertThat(Settings3.getCancelSubscriptionMinBeforeReadable()).isEqualTo("30分钟");
    }
}