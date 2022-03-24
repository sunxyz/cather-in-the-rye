package org.bitmagic.lab.reycatcher.support;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangrd
 */
public class ThreadLocalRcRequestContext implements RcRequestContext {

    private final ThreadLocal<Map<String,Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    @Override
    public void setAttr(String key, Object v) {
        getThreadLocalMap().put(key,v);
    }

    @Override
    public <T> T getAttr(String key, Class<T> tClass) {
       return (T) getAttr(key);
    }

    @Override
    public Object getAttr(String key) {
        return getThreadLocalMap().get(key);
    }

    @Override
    public boolean containsAttr(String key) {
        return getThreadLocalMap().containsKey(key);
    }

    @Override
    public void removeAttr(String key) {
        getThreadLocalMap().remove(key);
    }

    @Override
    public void clearAll() {
        getThreadLocalMap().clear();
    }

    private Map<String, Object> getThreadLocalMap() {
        return threadLocal.get();
    }
}
