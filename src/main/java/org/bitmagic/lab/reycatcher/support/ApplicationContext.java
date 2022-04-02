package org.bitmagic.lab.reycatcher.support;

import java.util.function.Function;

/**
 * @author yangrd
 */
public interface ApplicationContext {

    ApplicationContext INSTANT = new HashMapApplicationContext();

    void setAttirbute(String key, Object v);

    <T>T getAttirbute(String key, Class<T> tClass);

    <T> T computeIfAbsent(String key, Function<String,? extends T> gen);

    Object getAttirbute(String key);

    boolean containsAttirbute(String key);

    void removeAttirbute(String key);

    void clearAll();
}
