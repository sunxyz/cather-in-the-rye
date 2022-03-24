package org.bitmagic.lab.reycatcher.support;

/**
 * @author yangrd
 */
public class ApplicationContextHolder {

    private static ApplicationContext applicationContext = ApplicationContext.INSTANT;

    public static void init(ApplicationContext applicationContext){
        ApplicationContextHolder.applicationContext=applicationContext;
    }

    public static ApplicationContext getContext(){
        return applicationContext;
    }

    public static void clear(){
        applicationContext.clearAll();
    }
}
