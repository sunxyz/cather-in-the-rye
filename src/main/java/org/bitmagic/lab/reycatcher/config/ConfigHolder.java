package org.bitmagic.lab.reycatcher.config;

import org.bitmagic.lab.reycatcher.SessionContextHolder;
import org.bitmagic.lab.reycatcher.config.spring.RyeCatcherProperties;

import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigHolder {

    public static Function<String, RyeCatcherProperties.ConfigInfo> delegate;

    public static Supplier<String> getGenTokenTypeDelegate;

    public static String getGenTokenType(){
        return getGenTokenTypeDelegate.get();
    }

    public static String getTokenName(){return getConfigInfo().getTokenName();}

    public static int getSessionTimeOutMillisecond(){return getConfigInfo().getSessionTimeOutMillisecond();}

    public static boolean isNeedSave(String tokenType){
        return getConfigInfo(tokenType).isNeedSave();
    }

    public static boolean isNeedOutClient(String tokenType){
        return getConfigInfo(tokenType).isNeedOutClient();
    }

    private static RyeCatcherProperties.ConfigInfo getConfigInfo() {
        return getConfigInfo(SessionContextHolder.getContext().getSession().getSessionToken().getType());
    }

    private static RyeCatcherProperties.ConfigInfo getConfigInfo(String tokenType) {
        return delegate.apply(tokenType);
    }
}
