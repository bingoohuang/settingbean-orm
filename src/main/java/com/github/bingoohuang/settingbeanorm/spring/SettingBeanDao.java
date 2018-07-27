package com.github.bingoohuang.settingbeanorm.spring;

import com.github.bingoohuang.settingbeanorm.SettingItem;
import org.n3r.eql.eqler.annotations.Dynamic;
import org.n3r.eql.eqler.annotations.Eqler;
import org.n3r.eql.eqler.annotations.Sql;
import org.n3r.eql.eqler.annotations.SqlOptions;

import java.util.List;

@Eqler
public interface SettingBeanDao {
    @Sql("SELECT NAME, VALUE, TITLE, EDITABLE, SPEC, CREATE_TIME, UPDATE_TIME FROM $$")
    List<SettingItem> querySettingItems(@Dynamic String settingTable);

    @SqlOptions(iterate = true)
    @Sql("UPDATE $$ SET VALUE = #?# WHERE NAME = #?#")
    void updateSettings(List<SettingItem> items, @Dynamic String settingTable);

    @SqlOptions(iterate = true)
    @Sql("INSERT INTO $$(NAME, VALUE, TITLE, EDITABLE, UPDATE_TIME, CREATE_TIME) VALUES(#?#, #?#, #?#, #?#, #?#, #?# )")
    void addSettings(List<SettingItem> items, @Dynamic String settingTable);
}
