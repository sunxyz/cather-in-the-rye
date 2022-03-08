package org.bitmagic.lab.reycatcher.config;

import org.bitmagic.lab.reycatcher.config.spring.RyeCatcherProperties;

import java.util.function.Supplier;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class ConfigHolder {

    public static Supplier<RyeCatcherProperties.CertificationSystemInfo> delegate;

    public static String getGenTokenType(){
        return getConfigInfo().getGenTokenType();
    }

    public static String getTokenName(){return getConfigInfo().getTokenName();}

    public static int getSessionTimeOutMillisecond(){return getConfigInfo().getSessionTimeOutMillisecond();}

    public static boolean isNeedSave(){
        return getConfigInfo().isNeedSave();
    }

    public static boolean isNeedOutClient(){
        return getConfigInfo().isNeedOutClient();
    }

    public static boolean isMultipleUsers(){
        return  getConfigInfo().isMultipleUsers();
    }

    private static RyeCatcherProperties.CertificationSystemInfo getConfigInfo() {
        return delegate.get();
    }

}
