# settingbean-orm
Map setting javabean fields  to database setting table rows.
<br/>
[![Build Status](https://travis-ci.org/bingoohuang/settingbean-orm.svg?branch=master)](https://travis-ci.org/bingoohuang/settingbean-orm)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.github.bingoohuang%3Asettingbean-orm)](https://sonarqube.com/dashboard/index/com.github.bingoohuang%3Asettingbean-orm)
[![Coverage Status](https://coveralls.io/repos/github/bingoohuang/settingbean-orm/badge.svg?branch=master)](https://coveralls.io/github/bingoohuang/settingbean-orm?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/settingbean-orm/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.github.bingoohuang/settingbean-orm/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)


## Example

### Create Table SQL.
The table name can be customized, but the fields are required fixed as the following:

```sql

-- mysql
DROP TABLE IF EXISTS `t_setting`;
CREATE TABLE `t_setting` (
  `NAME` varchar(100) NOT NULL COMMENT '设置名',
  `VALUE` varchar(100) NOT NULL COMMENT '设置值',
  `TITLE` varchar(100) NOT NULL COMMENT '设置标题，用于页面展示',
  `EDITABLE` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否可以通过页面编辑',
  `SPEC` varchar(100) DEFAULT NULL COMMENT '取值校验规则，目前支持@Digits @Min(1) @Max(100) @Regex等',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`NAME`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '设置表';


```

### Demo table rows.

![image](https://user-images.githubusercontent.com/1940588/43242817-c4d9a4b2-90d5-11e8-86be-d5bfdea74c61.png)


### Programming
1. Define Javabean for setting.
    
    The bean's each field represent a setting for the system which can be mapping to 
    the setting table row.
    ```java
    public class XyzSetting {
        private int maxSubscribesPerMember;
        private boolean allowQueuing;

        @SettingField(name = "CANCEL_SUBSCRIPTION_MIN_BEFORE_HOURS",
                format = SettingValueFormat.HumanTimeDuration, timeUnit = TimeUnit.MINUTES)
        private int cancelSubscriptionMinBeforeMinutes = 0;  // 取消预约最少提前的小时数

        @SettingField(ignored = true)
        private String cancelSubscriptionMinBeforeReadable;  // 取消预约最少提前的小时数

        @SettingField(format = SettingValueFormat.SimpleList)
        private List<String> themes = ImmutableList.of("#333");  // 场馆可选主题色列表
    }
    ```

1. Define the setting service
    ```java
    @Service
    public class XyzSettingService extends SettingService<XyzSetting> {

        @Autowired
        public XyzSettingService(XyzBeanDao dao, XyzBeanCache cache) {
            // Here the customized table name is set to t_setting
            super(XyzSetting.class, "t_setting", new SettingUpdater(dao, cache));
        }
    }
    ```
1. Define the data access object
    ```java
    @Eqler
    public interface XyzBeanDao extends SettingBeanDao {
    }
    ```
1. Define the setting cache if required
    ```java
    @Service
    public class XyzBeanCache implements SettingCacheable {
        @Autowired XyzBeanDao xyzBeanDao;

        @WestCacheable(manager = "redis")
        @Override public <T> T getSettings(Class<T> beanClass, String settingTable) {
            val items = xyzBeanDao.querySettingItems(settingTable);
            T t = SettingUtil.populateBean(beanClass, items);
            return SettingUtil.autowire(t);
        }
    }
    ```
1. Use the setting bean where required
    ```java
    @Service
    public class MyService {
        @Autowired XyzSettingService xyzSettingService;


        public void addQueueing() {
            val settings = xyzSettingService.getSettingBean();
            if (settings.isAllowQueuing()) {
                // do queueing hrere
            }

        }
    }
    ```
