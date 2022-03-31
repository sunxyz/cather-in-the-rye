package org.bitmagic.lab.reycatcher.config;

import org.bitmagic.lab.reycatcher.func.PathMatcher;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author yangrd
 * @date 2022/03/06
 */
public class InstanceHolder {
    static Map<String, Object> beans = new HashMap<>(8);

    static {
        beans.put("antPathMatcher", (PathMatcher) new AntPathMatcher()::match);
        beans.put("colonPathMatcher", (PathMatcher) new AntPathMatcher(":")::match);
    }

    public static Function<Class<?>, Object> delegate;

    public static BiFunction<String, Class<?>, Object> delegate2;

    public static <T> T getInstance(Class<T> tClass) {
        return (T) delegate.apply(tClass);
    }

    public static <T> T getInstance(String beanName, Class<T> tClass) {
        return (T) beans.getOrDefault(beanName, delegate2.apply(beanName, tClass));
    }
}
