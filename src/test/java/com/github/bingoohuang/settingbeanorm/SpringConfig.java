package com.github.bingoohuang.settingbeanorm;


import com.github.bingoohuang.westcache.spring.WestCacheableEnabled;
import com.github.bingoohuang.westcache.utils.FastJsons;
import lombok.val;
import org.n3r.eql.eqler.spring.EqlerScan;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@ComponentScan(basePackageClasses = {SpringConfig.class})
@Configuration
@EqlerScan
@WestCacheableEnabled
public class SpringConfig {
    private RedisServer redisServer;

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        val creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Bean @Primary
    public Jedis thisJedisCommands() {
        FastJsons.json(null);
        return new Jedis("127.0.0.1", EmbeddedRedis.port);
    }


    @PostConstruct
    public void startRedis() {
        redisServer = new RedisServer(EmbeddedRedis.port);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }

    @Bean
    public SettingUpdater settingUpdater(@Autowired XyzBeanDao dao, @Autowired XyzBeanCache xyzBeanCache) {
        return new SettingUpdater(dao, xyzBeanCache);
    }

}