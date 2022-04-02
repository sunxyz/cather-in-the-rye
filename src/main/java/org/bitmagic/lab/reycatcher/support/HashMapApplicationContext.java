package org.bitmagic.lab.reycatcher.support;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HashMapApplicationContext implements ApplicationContext{

    private final Map<String,Object> map = new HashMap<>();

    @Override
    public void setAttirbute(String key, Object v) {
        map.put(key,v);
    }

    @Override
    public <T> T getAttirbute(String key, Class<T> tClass) {
        return (T) getAttirbute(key);
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, ? extends T> gen) {
        return (T) map.computeIfAbsent(key, gen);
    }

    @Override
    public Object getAttirbute(String key) {
        return map.get(key);
    }

    @Override
    public boolean containsAttirbute(String key) {
        return map.containsKey(key);
    }

    @Override
    public void removeAttirbute(String key) {
        map.remove(key);
    }

    @Override
    public void clearAll() {
        map.clear();
    }
}
