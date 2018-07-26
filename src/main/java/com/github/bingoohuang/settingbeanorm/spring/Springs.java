package com.github.bingoohuang.settingbeanorm.spring;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class Springs implements ApplicationContextAware {
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        appContext = applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) appContext.getBean(beanName);
    }


    public static <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }

    public static <T> T inject(T bean) {
        appContext.getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;
    }
}
