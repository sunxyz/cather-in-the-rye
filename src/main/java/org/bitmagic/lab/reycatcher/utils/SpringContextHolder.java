package org.bitmagic.lab.reycatcher.utils;


import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    public SpringContextHolder() {
    }

    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    public static <T> T getBean(String beanName, Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(beanName, requiredType);
    }

    public static void clearHolder() {
        applicationContext = null;
    }

    private static void assertContextInjected() {
        Assert.notNull(applicationContext, "applicationContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
    }

    @PreDestroy
    public void destroy() {
        clearHolder();
    }
}
