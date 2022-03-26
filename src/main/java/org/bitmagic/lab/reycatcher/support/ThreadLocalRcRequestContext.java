package org.bitmagic.lab.reycatcher.support;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author yangrd
 */
@RequiredArgsConstructor(staticName = "of")
public class ThreadLocalRcRequestContext implements RcRequestContext {

    private static final ThreadLocal<Map<String,Object>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);
    private final Supplier<HttpServletRequest> httpServletRequestSupplier;
    private final Supplier<HttpServletResponse> httpServletResponseSupplier;

    @Override
    public HttpServletRequest getRequest() {
        return httpServletRequestSupplier.get();
    }

    @Override
    public HttpServletResponse getResponse() {
        return httpServletResponseSupplier.get();
    }

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
        return THREAD_LOCAL.get();
    }
}
