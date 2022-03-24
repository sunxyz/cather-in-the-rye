package org.bitmagic.lab.reycatcher.support;

import java.util.function.Function;

/**
 * @author yangrd
 */
public interface ApplicationContext {

    ApplicationContext INSTANT = new HashMapApplicationContext();

    void setAttr(String key, Object v);

    <T>T getAttr(String key, Class<T> tClass);

    <T> T computeIfAbsent(String key, Function<String,? extends T> gen);

    Object getAttr(String key);

    boolean containsAttr(String key);

    void removeAttr(String key);

    void clearAll();
}
