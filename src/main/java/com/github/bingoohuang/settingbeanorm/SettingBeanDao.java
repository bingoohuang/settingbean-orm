package com.github.bingoohuang.settingbeanorm;

import org.n3r.eql.eqler.annotations.Dynamic;
import org.n3r.eql.eqler.annotations.Sql;
import org.n3r.eql.eqler.annotations.SqlOptions;

import java.util.List;

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
