package org.bitmagic.lab.reycatcher.support;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangrd
 */
@RequiredArgsConstructor(staticName = "create")
public class ThreadLocalRcRequestContext implements RcRequestContext {

    private static final String REQUEST="request";
    private static final String RESPONSE="response";

    private static final ThreadLocal<Map<String,Object>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);


    @Override
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) getThreadLocalMap().get(REQUEST);
    }

    @Override
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) getThreadLocalMap().get(RESPONSE);
    }

    public RcRequestContext init(HttpServletRequest request, HttpServletResponse response) {
        setRequest(request);
        setResponse(response);
        return this;
    }

    public void setRequest(HttpServletRequest request) {
        getThreadLocalMap().put(REQUEST, request);
    }

    public void setResponse(HttpServletResponse response) {
        getThreadLocalMap().put(RESPONSE, response);
    }

    @Override
    public void setAttribute(String key, Object v) {
        getThreadLocalMap().put(key,v);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> tClass) {
       return (T) getAttribute(key);
    }

    @Override
    public Object getAttribute(String key) {
        return getThreadLocalMap().get(key);
    }

    @Override
    public boolean containsAttribute(String key) {
        return getThreadLocalMap().containsKey(key);
    }

    @Override
    public void removeAttribute(String key) {
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
