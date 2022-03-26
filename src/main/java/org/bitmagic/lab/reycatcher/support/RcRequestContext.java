package org.bitmagic.lab.reycatcher.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yangrd
 */
public interface RcRequestContext {

    HttpServletRequest getRequest();

    HttpServletResponse getResponse();

    void setAttr(String key, Object v);

    <T> T getAttr(String key, Class<T> tClass);

    Object getAttr(String key);

    boolean containsAttr(String key);

    void removeAttr(String key);

    void clearAll();
}
