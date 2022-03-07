package org.bitmagic.lab.reycatcher.config;

import java.util.function.Function;

public class InstanceHolder {

    public static Function<Class<?>,Object> delegate;

    public static <T> T getInstance(Class<T> tClass){
        return (T)delegate.apply(tClass);
    }
}
