package com.github.bingoohuang.settingbeanorm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class BusinessTime {
    private String openTime;
    private String closeTime;
}
