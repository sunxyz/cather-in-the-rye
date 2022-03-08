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

    public static String getOutClientTokenName(){return getConfigInfo().getOutClientTokenName();}

    public static int getSessionTimeOutMillisecond(){return getConfigInfo().getSessionTimeOutMillisecond();}

    public static boolean isNeedSave(){
        return getConfigInfo().isSessionNeedSave();
    }

    public static boolean isNeedOutClient(){
        return getConfigInfo().isSessionNeedOutClient();
    }

    public static boolean isLoginMutex(){
        return  getConfigInfo().isLoginMutex();
    }

    private static RyeCatcherProperties.CertificationSystemInfo getConfigInfo() {
        return delegate.get();
    }

}
