package com.github.bingoohuang.settingbeanorm.test;

import com.github.bingoohuang.settingbeanorm.SettingItem;
import com.github.bingoohuang.settingbeanorm.SettingUpdateEvent;
import com.github.bingoohuang.settingbeanorm.SpringConfig;
import com.github.bingoohuang.settingbeanorm.spring.SettingService;
import com.github.bingoohuang.settingbeanorm.util.BusinessTime;
import com.github.bingoohuang.settingbeanorm.xyz.XyzSetting;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
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
        new Eql().execute("create table T_SETTING (" +
                "NAME varchar(100) not null, " +
                "VALUE varchar(100) null, " +
                "TITLE varchar(100) not null, " +
                "EDITABLE int not null default 1, " +
                "SPEC varchar(100) null, " +
                "CREATE_TIME timestamp not null, " +
                "UPDATE_TIME timestamp not null);");

        new Eql().execute(
                "insert into T_SETTING values('maxSubscribesPerMember', '10', '单会员最大订课数量', 1, '@Digits @Min(1) @Max(20) @Regex(\\d+)', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into T_SETTING values('allowQueuing', '1', '是否开启排队', 1, '@Boolean @Enum(true,false)', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into T_SETTING values('CANCEL_SUBSCRIPTION_MIN_BEFORE_HOURS', '0.5h', '至少提前多少时间取消预订', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into T_SETTING values('themes', 'red,blue,green', '主题颜色', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into T_SETTING values('cardTypeNamesInShowOrder', '', '测试', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
                , "insert into T_SETTING values('personalQrCode', null, '测试', 1, '', '2018-04-25 13:15:45', '2018-04-25 13:15:45')"
        );
    }

    @Autowired SettingService settingService;

    @Test
    public void cache() {
        XyzSetting settings = settingService.getSettingBean();
        settings.setAllowQueuing(!settings.isAllowQueuing());

        val settings2 = settingService.getSettingBean();
        assertThat(settings).isNotSameAs(settings2);
        assertThat(settings).isNotEqualTo(settings2);
    }


    @Test
    public void getSettings() {
        XyzSetting setting = settingService.getSettingBean();
        val other = XyzSetting.builder().maxSubscribesPerMember(10).allowQueuing(true).xx(100)
                .cancelSubscriptionMinBeforeMinutes(30).cancelSubscriptionMinBeforeReadable("30分钟")
                .themes(Lists.newArrayList("red", "blue", "green"))
                .businessTime(new BusinessTime("09:00", "19:00"))
                .build();
        assertThat(setting).isEqualTo(other);

        setting.setMaxSubscribesPerMember(11);
        setting.setAllowQueuing(false);

        SettingUpdateReceiver receiver = new SettingUpdateReceiver();
        settingService.registerSettingUpdate(receiver);

        settingService.updateSettings(setting);

        assertThat(receiver.event.getOldSettingBbean().getMaxSubscribesPerMember()).isEqualTo(10);
        assertThat(receiver.event.getNewSettingBean().getMaxSubscribesPerMember()).isEqualTo(11);
        assertThat(receiver.event.getOldSettingBbean().isAllowQueuing()).isTrue();
        assertThat(receiver.event.getNewSettingBean().isAllowQueuing()).isFalse();

        settingService.unregisterSettingChange(receiver);

        XyzSetting setting2 = settingService.getSettingBean();
        other.setMaxSubscribesPerMember(11);
        other.setAllowQueuing(false);
        assertThat(setting2).isEqualTo(other);

        val SettingsItems = settingService.getSettingsItems();
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

        settingService.updateSettings(Lists.newArrayList(item1));

        XyzSetting Settings3 = settingService.getSettingBean();
        assertThat(Settings3.getMaxSubscribesPerMember()).isEqualTo(12);
        assertThat(Settings3.getXx()).isEqualTo(100);
        assertThat(Settings3.getCancelSubscriptionMinBeforeMinutes()).isEqualTo(30);
        assertThat(Settings3.getCancelSubscriptionMinBeforeReadable()).isEqualTo("30分钟");
    }

    public static class SettingUpdateReceiver {
        SettingUpdateEvent<XyzSetting> event;

        @Subscribe
        public void subscribe(SettingUpdateEvent<XyzSetting> event) {
            this.event = event;
        }
    }
}