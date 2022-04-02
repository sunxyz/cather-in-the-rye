package org.bitmagic.lab.reycatcher.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangrd
 */
public interface RcRequestContext {

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    void setAttribute(String key, Object v);

    <T> T getAttribute(String key, Class<T> tClass);

    Object getAttribute(String key);

    boolean containsAttribute(String key);

    void removeAttribute(String key);

    void clearAll();
}
